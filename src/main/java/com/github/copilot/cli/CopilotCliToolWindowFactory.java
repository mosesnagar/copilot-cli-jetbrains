package com.github.copilot.cli;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalView;

import javax.swing.*;
import java.awt.*;

public class CopilotCliToolWindowFactory implements ToolWindowFactory {
    public static final String TOOL_WINDOW_ID = "Copilot CLI";
    public static final com.intellij.openapi.util.Key<ShellTerminalWidget> WIDGET_KEY =
            com.intellij.openapi.util.Key.create("COPILOT_CLI_WIDGET");
    private static final com.intellij.openapi.util.Key<Boolean> AUTORUN_DONE_KEY =
            com.intellij.openapi.util.Key.create("COPILOT_CLI_AUTORUN_DONE");

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Check if CLI is installed first
        if (!CopilotCliUtils.isCopilotCliInstalled()) {
            CopilotCliUtils.notify(project, 
                "Copilot CLI not found. " + CopilotCliUtils.getInstallInstructions(), 
                NotificationType.WARNING);
            toolWindow.setAvailable(false);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout());
        String workDir = project.getBasePath() != null ? project.getBasePath() : System.getProperty("user.home");

        // Create terminal widget using TerminalView, then hide bottom Terminal tool window
        ShellTerminalWidget widget = TerminalView.getInstance(project)
                .createLocalShellWidget(workDir, TOOL_WINDOW_ID);

        panel.add(widget.getComponent(), BorderLayout.CENTER);

        Content content = ContentFactory.getInstance().createContent(panel, "", false);
        content.putUserData(WIDGET_KEY, widget);
        toolWindow.getContentManager().addContent(content);

        // Hide bottom Terminal tool window to avoid showing two terminals
        ToolWindow term = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (term != null && term.isVisible()) {
            term.hide(null);
        }

        if (Boolean.TRUE.equals(content.getUserData(AUTORUN_DONE_KEY))) return;
        content.putUserData(AUTORUN_DONE_KEY, true);

        ApplicationManager.getApplication().invokeLater(() -> autorun(project, widget, workDir));
    }

    private void autorun(Project project, ShellTerminalWidget widget, String workDir) {
        // Just run copilot - the terminal will show an error if not installed
        CopilotCliUtils.exec(project, widget, "copilot");
    }
}
