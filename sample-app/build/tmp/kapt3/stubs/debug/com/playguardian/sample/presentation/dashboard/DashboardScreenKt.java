package com.playguardian.sample.presentation.dashboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000P\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a,\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0010\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\tH\u0007\u001a\u0010\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\fH\u0007\u001a \u0010\r\u001a\u00020\u00012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\f0\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0007\u001a\u0017\u0010\u0012\u001a\u00020\u00012\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0007\u00a2\u0006\u0002\u0010\u0015\u001a\u001c\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00190\u00172\u0006\u0010\u001a\u001a\u00020\u001bH\u0002\u00a8\u0006\u001c"}, d2 = {"AccessStatusCard", "", "info", "Lcom/playguardian/sample/domain/model/AccessInfo;", "onAudit", "Lkotlin/Function0;", "onRequest", "DashboardScreen", "viewModel", "Lcom/playguardian/sample/presentation/dashboard/DashboardViewModel;", "LogItem", "entry", "Lcom/playguardian/sample/presentation/dashboard/LogEntry;", "LogPanel", "logs", "", "modifier", "Landroidx/compose/ui/Modifier;", "StatusIndicator", "isGranted", "", "(Ljava/lang/Boolean;)V", "getDefaults", "Lkotlin/Pair;", "", "Lcom/playguardian/oem/AppCategory;", "access", "Lcom/playguardian/oem/SpecialAccess;", "sample-app_debug"})
public final class DashboardScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DashboardScreen(@org.jetbrains.annotations.NotNull()
    com.playguardian.sample.presentation.dashboard.DashboardViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void AccessStatusCard(@org.jetbrains.annotations.NotNull()
    com.playguardian.sample.domain.model.AccessInfo info, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAudit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRequest) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StatusIndicator(@org.jetbrains.annotations.Nullable()
    java.lang.Boolean isGranted) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void LogPanel(@org.jetbrains.annotations.NotNull()
    java.util.List<com.playguardian.sample.presentation.dashboard.LogEntry> logs, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void LogItem(@org.jetbrains.annotations.NotNull()
    com.playguardian.sample.presentation.dashboard.LogEntry entry) {
    }
    
    private static final kotlin.Pair<java.lang.String, com.playguardian.oem.AppCategory> getDefaults(com.playguardian.oem.SpecialAccess access) {
        return null;
    }
}