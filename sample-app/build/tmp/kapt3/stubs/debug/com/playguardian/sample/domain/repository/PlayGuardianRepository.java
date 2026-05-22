package com.playguardian.sample.domain.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u000bH&J \u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0011H&J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0004\u001a\u00020\u0005H&J0\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u0011H&\u00a8\u0006\u0015"}, d2 = {"Lcom/playguardian/sample/domain/repository/PlayGuardianRepository;", "", "audit", "Lcom/playguardian/audit/model/PolicyReport;", "access", "Lcom/playguardian/oem/SpecialAccess;", "reason", "", "category", "Lcom/playguardian/oem/AppCategory;", "auditManifest", "Lcom/playguardian/audit/model/ManifestAuditReport;", "handleResume", "", "activity", "Landroid/app/Activity;", "callback", "Lcom/playguardian/core/AccessCallback;", "isGranted", "", "requestAccess", "sample-app_debug"})
public abstract interface PlayGuardianRepository {
    
    public abstract boolean isGranted(@org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access);
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.playguardian.audit.model.PolicyReport audit(@org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.AppCategory category);
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.playguardian.audit.model.ManifestAuditReport auditManifest();
    
    public abstract void requestAccess(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.AppCategory category, @org.jetbrains.annotations.NotNull()
    com.playguardian.core.AccessCallback callback);
    
    public abstract void handleResume(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    com.playguardian.core.AccessCallback callback);
}