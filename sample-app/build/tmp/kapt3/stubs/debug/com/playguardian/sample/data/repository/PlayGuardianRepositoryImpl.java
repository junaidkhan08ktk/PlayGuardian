package com.playguardian.sample.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\r\u001a\u00020\u000eH\u0016J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0007\u001a\u00020\bH\u0016J0\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/playguardian/sample/data/repository/PlayGuardianRepositoryImpl;", "Lcom/playguardian/sample/domain/repository/PlayGuardianRepository;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "audit", "Lcom/playguardian/audit/model/PolicyReport;", "access", "Lcom/playguardian/oem/SpecialAccess;", "reason", "", "category", "Lcom/playguardian/oem/AppCategory;", "auditManifest", "Lcom/playguardian/audit/model/ManifestAuditReport;", "handleResume", "", "activity", "Landroid/app/Activity;", "callback", "Lcom/playguardian/core/AccessCallback;", "isGranted", "", "requestAccess", "sample-app_debug"})
public final class PlayGuardianRepositoryImpl implements com.playguardian.sample.domain.repository.PlayGuardianRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    @javax.inject.Inject()
    public PlayGuardianRepositoryImpl(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    public boolean isGranted(@org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access) {
        return false;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.playguardian.audit.model.PolicyReport audit(@org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.AppCategory category) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.playguardian.audit.model.ManifestAuditReport auditManifest() {
        return null;
    }
    
    @java.lang.Override()
    public void requestAccess(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.AppCategory category, @org.jetbrains.annotations.NotNull()
    com.playguardian.core.AccessCallback callback) {
    }
    
    @java.lang.Override()
    public void handleResume(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    com.playguardian.core.AccessCallback callback) {
    }
}