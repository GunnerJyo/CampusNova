package com.campusnova.service;

import com.campusnova.config.CollegeProfile;
import com.campusnova.model.OfficialKnowledge;
import com.campusnova.repository.OfficialKnowledgeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

/** Admin-triggered importer; student chat never fetches an external page. */
@Service
public class OfficialKnowledgeImportService {
    private static final int MAX_SECTION_LENGTH = 7000;
    private final OfficialKnowledgeRepository repository;
    private final CollegeProfile profile;

    public OfficialKnowledgeImportService(OfficialKnowledgeRepository repository, CollegeProfile profile) { this.repository = repository; this.profile = profile; }
    public List<OfficialKnowledge> importCorePages() {
        String[] paths = {"", "college/", "departments/", "programs/", "admission-keam/", "admission-procedure/", "central-library/", "bus-service/", "hostel/", "administrative-wing/", "principal/", "college-map/", "placement-cell/"};
        List<OfficialKnowledge> imported = new ArrayList<OfficialKnowledge>();
        for (String path : paths) try { imported.addAll(importPage(profile.getOfficialDomain() + path)); } catch (IOException ignored) { /* continue */ }
        return imported;
    }
    /** Retains the established single-record API for a manual one-page import. */
    public OfficialKnowledge importUrl(String source) throws IOException { List<OfficialKnowledge> sections=importPage(source); if(sections.isEmpty())throw new IOException("No meaningful content was found on the official page"); return sections.get(0); }
    private List<OfficialKnowledge> importPage(String source) throws IOException {
        String canonical=canonicalOfficialUrl(source); List<ExtractedSection> sections=extractSections(canonical,fetch(canonical));
        if(sections.isEmpty())throw new IOException("No meaningful main content was found on the official page");
        // Only remove old importer records that represented an entire page. Seeded and manually curated records remain.
        repository.deleteAll(repository.findBySourceUrlAndPageType(canonical,"WEB_PAGE"));
        Set<String> activeUrls=new HashSet<String>(); List<OfficialKnowledge> saved=new ArrayList<OfficialKnowledge>();
        for(ExtractedSection section:sections){String sectionUrl=canonical+"#campusnova-"+slug(section.heading);activeUrls.add(sectionUrl);OfficialKnowledge record=repository.findBySourceUrl(sectionUrl).orElse(new OfficialKnowledge(sectionUrl,section.pageTitle+" — "+section.heading,"WEB_PAGE",categoryFor(canonical),section.text));record.setSourceUrl(sectionUrl);record.setSourcePageUrl(canonical);record.setSectionHeading(section.heading);record.setTitle(section.pageTitle+" — "+section.heading);record.setPageType("WEB_PAGE");record.setCategory(categoryFor(canonical));record.setExtractedText(section.text);record.setOfficialSource(true);record.setVerified(true);record.setActive(true);record.setImportedAt(LocalDateTime.now());record.setVerifiedAt(LocalDateTime.now());record.setSourceUpdatedDate(LocalDateTime.now());record.setSourcePriority(sourcePriority(canonical));record.setChecksum(hash(section.text));record.setContentVersion(record.getChecksum().substring(0,12));saved.add(repository.save(record));}
        for(OfficialKnowledge old:repository.findBySourcePageUrl(canonical))if("WEB_PAGE".equals(old.getPageType())&&!activeUrls.contains(old.getSourceUrl())){old.setActive(false);repository.save(old);}
        // A previous importer stored an entire page at its canonical URL. Retire only a record that makes the obsolete principal claim.
        if(canonical.contains("/principal/"))for(OfficialKnowledge old:repository.findAll())if(old.isOfficialSource()&&old.isActive()&&old.getExtractedText()!=null&&old.getExtractedText().toLowerCase(Locale.ROOT).contains("k. v. anil kumar")&&old.getExtractedText().toLowerCase(Locale.ROOT).contains("principal")){old.setActive(false);repository.save(old);} return saved;
    }
    String canonicalOfficialUrl(String source) throws IOException { URL url=new URL(source);URL allowed=new URL(profile.getOfficialDomain());if(!url.getHost().equalsIgnoreCase(allowed.getHost())||!("https".equalsIgnoreCase(url.getProtocol())||"http".equalsIgnoreCase(url.getProtocol())))throw new IllegalArgumentException("Only the configured official LBSCEK domain can be imported");String path=url.getPath()==null?"":url.getPath();if(path.isEmpty())path="/";return url.getProtocol()+"://"+url.getHost()+path; }
    List<ExtractedSection> extractSections(String source,String html) {
        Document document=Jsoup.parse(html,source);document.select("script, style, noscript, nav, header, footer, aside, form, svg, iframe, .menu, .navbar, .navigation, .breadcrumb, .sidebar, .widget, .cookie, .social, .share, .footer, .header").remove();String pageTitle=clean(document.title());if(pageTitle.isEmpty())pageTitle="LBSCEK official information";Element root=mainContent(document);if(root==null)return Collections.emptyList();
        List<ExtractedSection> result=new ArrayList<ExtractedSection>();
        // Leadership pages often put the current office holder in profile divs rather than paragraphs.
        // Capture that profile independently, stopping before CV/history sections, without encoding any person's name.
        if(source.contains("/principal/")){String profileText=clean(root.text());int boundary=profileText.toLowerCase(Locale.ROOT).indexOf("educational qualifications");if(boundary>0)profileText=profileText.substring(0,boundary).trim();if(meaningful(profileText))result.add(new ExtractedSection(pageTitle,"Current Principal",profileText));}
        String heading=null;StringBuilder text=new StringBuilder();for(Element element:root.select("h1,h2,h3,h4,p,li,tr")){String value=clean(element.text());if(value.isEmpty()||navigationLabel(value))continue;if(element.tagName().matches("h[1-4]")){addSection(result,pageTitle,heading,text);heading=value;text.setLength(0);}else append(text,element.tagName().equals("li")?"• "+value:value);}addSection(result,pageTitle,heading,text);if(result.isEmpty()){String fallback=clean(root.text());if(meaningful(fallback)&&!navigationLabel(fallback))result.add(new ExtractedSection(pageTitle,"Overview",fallback));}return result;
    }
    private Element mainContent(Document document){Element best=null;for(Element candidate:document.select("main, article, .entry-content, .page-content, .kingster-page-content, .gdlr-core-page-builder-body"))if(best==null||candidate.text().length()>best.text().length())best=candidate;return best==null?document.body():best;}
    private void addSection(List<ExtractedSection> sections,String pageTitle,String heading,StringBuilder text){String content=clean(text.toString());if(heading!=null&&meaningful(content)&&!navigationLabel(heading)&&!navigationLabel(content))sections.add(new ExtractedSection(pageTitle,heading,content));}
    private void append(StringBuilder target,String value){if(target.length()>=MAX_SECTION_LENGTH)return;if(target.length()>0)target.append(' ');target.append(value);}
    private boolean meaningful(String value){return value.length()>=45&&value.split("\\s+").length>=8;}
    private boolean navigationLabel(String value){String n=value.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]"," ").trim();return n.matches("(home|about|academics|admissions?|departments?|facilities|contact|login|placement|research|students?|more|menu|close|search|quick links?|important links?|follow us|copyright)( (home|about|academics|admissions?|departments?|facilities|contact|login|placement|research|students?|more|menu|close|search|quick links?|important links?|follow us|copyright))*");}
    private String fetch(String source)throws IOException{HttpURLConnection connection=(HttpURLConnection)new URL(source).openConnection();connection.setConnectTimeout(8000);connection.setReadTimeout(12000);connection.setRequestProperty("User-Agent","CampusNova official knowledge importer/2.0");try(InputStream in=connection.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] buffer=new byte[4096];int count;while((count=in.read(buffer))!=-1)out.write(buffer,0,count);return new String(out.toByteArray(),StandardCharsets.UTF_8);}}
    private String clean(String value){return value==null?"":Jsoup.parse(value).text().replaceAll("\\s+"," ").trim();}
    private String categoryFor(String url){String s=url.toLowerCase(Locale.ROOT);if(s.contains("library"))return "Library";if(s.contains("bus"))return "Transport";if(s.contains("hostel"))return "Hostel";if(s.contains("placement"))return "Placements";if(s.contains("admission"))return "Admissions";if(s.contains("principal")||s.contains("administrative"))return "Administration";if(s.contains("department")||s.contains("program"))return "Academics";return "College Information";}
    private int sourcePriority(String url){if(url.contains("/principal/"))return 100;if(url.contains("/admission-")||url.contains("/central-library/")||url.contains("/bus-service/"))return 80;return 60;}
    private String slug(String value){String s=value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","-").replaceAll("(^-|-$)","");return(s.isEmpty()?hash(value).substring(0,12):s.substring(0,Math.min(s.length(),80)))+"-"+hash(value).substring(0,8);}
    private String hash(String value){try{byte[] bytes=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));StringBuilder out=new StringBuilder();for(byte b:bytes)out.append(String.format("%02x",b));return out.toString();}catch(Exception e){return "0000000000000000";}}
    static class ExtractedSection { final String pageTitle,heading,text; ExtractedSection(String pageTitle,String heading,String text){this.pageTitle=pageTitle;this.heading=heading;this.text=text;} }
}
