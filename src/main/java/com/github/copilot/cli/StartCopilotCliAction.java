package com.github.copilot.cli;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import java.awt.event.InputEvent;

public class StartCopilotCliAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        InputEvent ie = e.getInputEvent();
        boolean forceRestart = ie != null && (ie.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;

        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                ToolWindow tw = ToolWindowManager.getInstance(project)
                        .getToolWindow(CopilotCliToolWindowFactory.TOOL_WINDOW_ID);
                if (tw == null) {
                    CopilotCliUtils.notify(project, "Tool window not found: " + CopilotCliToolWindowFactory.TOOL_WINDOW_ID, NotificationType.ERROR);
                    return;
                }
                // Open and focus
                tw.activate(null, true);

                var cm = tw.getContentManager();

                // No content - create it (createToolWindowContent will autorun)
                if (cm.getContentCount() == 0) {
                    new CopilotCliToolWindowFactory().createToolWindowContent(project, tw);
                    return; // autorun will handle startup
                }

                // Shift = force restart: destroy and recreate content (clear history, autorun again)
                if (forceRestart) {
                    Content selected = cm.getSelectedContent();
                    if (selected != null) {
                        cm.removeContent(selected, true);
                    } else {
                        for (Content c : cm.getContents()) {
                            cm.removeContent(c, true);
                        }
                    }
                    new CopilotCliToolWindowFactory().createToolWindowContent(project, tw);
                    return;
                }

                // No restart: do nothing (session already started via autorun), just keep focus
            } catch (Throwable ex) {
                CopilotCliUtils.notify(project, "Execution failed: " + ex.getMessage(), NotificationType.ERROR);
            }
        });
    }
}
