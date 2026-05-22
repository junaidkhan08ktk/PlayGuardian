package com.playguardian.sample.presentation.dashboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001a\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0002J\u001e\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u0019J\u0006\u0010\u001a\u001a\u00020\u000fJ\u0006\u0010\u001b\u001a\u00020\u000fJ\u0016\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u0016J\u0006\u0010\u001f\u001a\u00020\u000fJ&\u0010 \u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u0019R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006!"}, d2 = {"Lcom/playguardian/sample/presentation/dashboard/DashboardViewModel;", "Landroidx/lifecycle/ViewModel;", "getAccessStatusesUseCase", "Lcom/playguardian/sample/domain/usecase/GetAccessStatusesUseCase;", "repository", "Lcom/playguardian/sample/domain/repository/PlayGuardianRepository;", "(Lcom/playguardian/sample/domain/usecase/GetAccessStatusesUseCase;Lcom/playguardian/sample/domain/repository/PlayGuardianRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/playguardian/sample/presentation/dashboard/DashboardState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addLog", "", "message", "", "level", "Lcom/playguardian/sample/presentation/dashboard/LogLevel;", "auditAccess", "access", "Lcom/playguardian/oem/SpecialAccess;", "reason", "category", "Lcom/playguardian/oem/AppCategory;", "auditManifest", "clearLogs", "handleResume", "activity", "Landroid/app/Activity;", "refreshStatuses", "requestAccess", "sample-app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class DashboardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.playguardian.sample.domain.usecase.GetAccessStatusesUseCase getAccessStatusesUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.playguardian.sample.domain.repository.PlayGuardianRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.playguardian.sample.presentation.dashboard.DashboardState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.playguardian.sample.presentation.dashboard.DashboardState> uiState = null;
    
    @javax.inject.Inject()
    public DashboardViewModel(@org.jetbrains.annotations.NotNull()
    com.playguardian.sample.domain.usecase.GetAccessStatusesUseCase getAccessStatusesUseCase, @org.jetbrains.annotations.NotNull()
    com.playguardian.sample.domain.repository.PlayGuardianRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.playguardian.sample.presentation.dashboard.DashboardState> getUiState() {
        return null;
    }
    
    public final void refreshStatuses() {
    }
    
    public final void auditAccess(@org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.AppCategory category) {
    }
    
    public final void requestAccess(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.AppCategory category) {
    }
    
    public final void auditManifest() {
    }
    
    public final void handleResume(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    com.playguardian.oem.SpecialAccess access) {
    }
    
    public final void clearLogs() {
    }
    
    private final void addLog(java.lang.String message, com.playguardian.sample.presentation.dashboard.LogLevel level) {
    }
}