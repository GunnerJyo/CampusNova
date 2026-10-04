package com.campusnova.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/** A verified, independently retrievable section from an official college page. */
@Entity
@Table(indexes = {
    @Index(name = "idx_official_knowledge_active", columnList = "active,officialSource"),
    @Index(name = "idx_official_knowledge_page", columnList = "sourcePageUrl")
})
public class OfficialKnowledge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Unique page URL plus a stable CampusNova section fragment. */
    @Column(unique = true, length = 1000) private String sourceUrl;
    @Column(length = 1000) private String sourcePageUrl;
    private String sectionHeading;
    private String title;
    private String pageType;
    private String category;
    @Column(length = 12000) private String extractedText;
    private LocalDateTime sourcePublishedDate;
    private LocalDateTime sourceUpdatedDate;
    private LocalDateTime importedAt = LocalDateTime.now();
    private boolean officialSource = true;
    private boolean verified = true;
    private boolean active = true;
    private String checksum;

    public OfficialKnowledge() { }
    public OfficialKnowledge(String url, String title, String type, String category, String text) {
        sourceUrl = url; this.title = title; pageType = type; this.category = category; extractedText = text;
    }
    public Long getId() { return id; }
    public String getSourceUrl() { return sourceUrl; } public void setSourceUrl(String v) { sourceUrl = v; }
    public String getSourcePageUrl() { return sourcePageUrl; } public void setSourcePageUrl(String v) { sourcePageUrl = v; }
    public String getSectionHeading() { return sectionHeading; } public void setSectionHeading(String v) { sectionHeading = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public String getPageType() { return pageType; } public void setPageType(String v) { pageType = v; }
    public String getCategory() { return category; } public void setCategory(String v) { category = v; }
    public String getExtractedText() { return extractedText; } public void setExtractedText(String v) { extractedText = v; }
    public LocalDateTime getImportedAt() { return importedAt; } public void setImportedAt(LocalDateTime v) { importedAt = v; }
    public LocalDateTime getSourcePublishedDate() { return sourcePublishedDate; } public void setSourcePublishedDate(LocalDateTime v) { sourcePublishedDate = v; }
    public LocalDateTime getSourceUpdatedDate() { return sourceUpdatedDate; } public void setSourceUpdatedDate(LocalDateTime v) { sourceUpdatedDate = v; }
    public boolean isOfficialSource() { return officialSource; } public void setOfficialSource(boolean v) { officialSource = v; }
    public boolean isVerified() { return verified; } public void setVerified(boolean v) { verified = v; }
    public boolean isActive() { return active; } public void setActive(boolean v) { active = v; }
    public String getChecksum() { return checksum; } public void setChecksum(String v) { checksum = v; }
}
