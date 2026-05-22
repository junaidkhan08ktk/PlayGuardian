package com.playguardian.sample;

/**
 * Main sample activity demonstrating every PlayGuardian API.
 *
 * Sections:
 * • Live status cards — show granted/denied for each access on resume
 * • Policy audit — run heuristic risk evaluation per access type
 * • Request access — full request → settings → resume verification flow
 * • Output log — timestamped, color-coded output for all events
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001/B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\f2\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u0010\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J\b\u0010\u0015\u001a\u00020\u0011H\u0002J\b\u0010\u0016\u001a\u00020\u0011H\u0002J\b\u0010\u0017\u001a\u00020\u0011H\u0002J\u0018\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0010\u0010\u001c\u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0010\u0010\u001f\u001a\u00020\u00112\u0006\u0010 \u001a\u00020\rH\u0002J\u0012\u0010!\u001a\u00020\u00112\b\u0010\"\u001a\u0004\u0018\u00010#H\u0014J\b\u0010$\u001a\u00020\u0011H\u0014J\b\u0010%\u001a\u00020\u0011H\u0002J\u0010\u0010&\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J\b\u0010\'\u001a\u00020\u0011H\u0002J\b\u0010(\u001a\u00020\u0011H\u0002J\f\u0010)\u001a\u00020\r*\u00020\u0006H\u0002J\u001b\u0010*\u001a\u00020\u0011*\u00020+2\b\u0010,\u001a\u0004\u0018\u00010-H\u0002\u00a2\u0006\u0002\u0010.R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00060"}, d2 = {"Lcom/playguardian/sample/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/playguardian/sample/databinding/ActivityMainBinding;", "pendingAccess", "Lcom/playguardian/oem/SpecialAccess;", "pendingCallback", "Lcom/playguardian/core/AccessCallback;", "timeFormat", "Ljava/text/SimpleDateFormat;", "accessDefaults", "Lkotlin/Pair;", "", "Lcom/playguardian/oem/AppCategory;", "access", "appendToLog", "", "ssb", "Landroid/text/SpannableStringBuilder;", "auditAccess", "clearLog", "clearPending", "logBlank", "logLine", "text", "level", "Lcom/playguardian/sample/MainActivity$LogLevel;", "logReport", "report", "Lcom/playguardian/audit/model/PolicyReport;", "logSection", "title", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "refreshStatusCards", "requestAccess", "runManifestAudit", "wireButtons", "displayName", "setStatus", "Landroid/widget/TextView;", "granted", "", "(Landroid/widget/TextView;Ljava/lang/Boolean;)V", "LogLevel", "sample-app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.playguardian.sample.databinding.ActivityMainBinding binding;
    
    /**
     * Tracks the most recently requested access so onResume can verify it.
     */
    @org.jetbrains.annotations.Nullable()
    private com.playguardian.oem.SpecialAccess pendingAccess;
    
    /**
     * Retained callback for the active request flow.
     */
    @org.jetbrains.annotations.Nullable()
    private com.playguardian.core.AccessCallback pendingCallback;
    @org.jetbrains.annotations.NotNull()
    private final java.text.SimpleDateFormat timeFormat = null;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    private final void refreshStatusCards() {
    }
    
    /**
     * null = N/A (not applicable on this API level)
     */
    private final void setStatus(android.widget.TextView $this$setStatus, java.lang.Boolean granted) {
    }
    
    private final void wireButtons() {
    }
    
    private final void auditAccess(com.playguardian.oem.SpecialAccess access) {
    }
    
    private final void runManifestAudit() {
    }
    
    private final void requestAccess(com.playguardian.oem.SpecialAccess access) {
    }
    
    private final kotlin.Pair<java.lang.String, com.playguardian.oem.AppCategory> accessDefaults(com.playguardian.oem.SpecialAccess access) {
        return null;
    }
    
    private final void logSection(java.lang.String title) {
    }
    
    private final void logLine(java.lang.String text, com.playguardian.sample.MainActivity.LogLevel level) {
    }
    
    private final void logBlank() {
    }
    
    private final void logReport(com.playguardian.audit.model.PolicyReport report) {
    }
    
    private final void appendToLog(android.text.SpannableStringBuilder ssb) {
    }
    
    private final void clearLog() {
    }
    
    private final void clearPending() {
    }
    
    private final java.lang.String displayName(com.playguardian.oem.SpecialAccess $this$displayName) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/playguardian/sample/MainActivity$LogLevel;", "", "(Ljava/lang/String;I)V", "INFO", "OK", "WARN", "ERROR", "sample-app_debug"})
    static enum LogLevel {
        /*public static final*/ INFO /* = new INFO() */,
        /*public static final*/ OK /* = new OK() */,
        /*public static final*/ WARN /* = new WARN() */,
        /*public static final*/ ERROR /* = new ERROR() */;
        
        LogLevel() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.playguardian.sample.MainActivity.LogLevel> getEntries() {
            return null;
        }
    }
}