package com.ecobazaar.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * UserChallengeProgress Entity
 * 
 * Tracks individual user's progress on eco challenges
 */
@Entity
@Table(name = "user_challenge_progress")
public class UserChallengeProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String challengeId;
    
    @Column(nullable = false)
    private Integer currentProgress = 0;
    
    @Column(nullable = false)
    private Double progressPercentage = 0.0;
    
    @Column(nullable = false)
    private Boolean isCompleted = false;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private Integer pointsEarned = 0;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Many-to-One relationship with EcoChallenge
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eco_challenge_id", referencedColumnName = "id")
    private EcoChallenge ecoChallenge;
    
    // Constructors
    public UserChallengeProgress() {}
    
    public UserChallengeProgress(String userId, String challengeId) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.startedAt = LocalDateTime.now();
    }
    
    public UserChallengeProgress(String userId, String challengeId, Integer currentProgress) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.currentProgress = currentProgress;
        this.startedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getChallengeId() {
        return challengeId;
    }
    
    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }
    
    public Integer getCurrentProgress() {
        return currentProgress;
    }
    
    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }
    
    public Double getProgressPercentage() {
        return progressPercentage;
    }
    
    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    
    public Boolean getIsCompleted() {
        return isCompleted;
    }
    
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
        if (isCompleted && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public Integer getPointsEarned() {
        return pointsEarned;
    }
    
    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public EcoChallenge getEcoChallenge() {
        return ecoChallenge;
    }
    
    public void setEcoChallenge(EcoChallenge ecoChallenge) {
        this.ecoChallenge = ecoChallenge;
    }
}