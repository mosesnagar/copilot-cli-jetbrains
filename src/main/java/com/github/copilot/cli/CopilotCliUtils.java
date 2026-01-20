package com.github.copilot.cli;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Shared utility methods for Copilot CLI plugin
 */
public final class CopilotCliUtils {
    private static final String NOTIF_GROUP_ID = "copilot.cli.notifications";
    private static final String CLI_COMMAND = "copilot";

    private CopilotCliUtils() {
        // Utility class - no instantiation
    }

    /**
     * Check if Copilot CLI is installed and available in PATH or common locations
     */
    public static boolean isCopilotCliInstalled() {
        // First try which/where command
        try {
            String[] cmd;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                cmd = new String[]{"where", CLI_COMMAND};
            } else {
                cmd = new String[]{"/bin/sh", "-c", "which " + CLI_COMMAND + " || command -v " + CLI_COMMAND};
            }
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            if (exitCode == 0) return true;
        } catch (Exception ignored) {}

        // Check common installation paths
        String[] commonPaths = {
            "/opt/homebrew/bin/copilot",
            "/usr/local/bin/copilot",
            "/usr/bin/copilot",
            System.getProperty("user.home") + "/.local/bin/copilot",
            System.getProperty("user.home") + "/bin/copilot"
        };
        
        for (String path : commonPaths) {
            File file = new File(path);
            if (file.exists() && file.canExecute()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get install instructions based on OS
     */
    public static String getInstallInstructions() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Install with: winget install GitHub.Copilot";
        } else {
            return "Install with: curl -fsSL https://gh.io/copilot-install | bash (or npm install -g @github/copilot)";
        }
    }

    /**
     * Get the CLI command name
     */
    public static String getCliCommand() {
        return CLI_COMMAND;
    }

    /**
     * Shell-quote a string for safe execution
     */
    public static String shQuote(String s) {
        if (s == null) return "''";
        return "'" + s.replace("'", "'\\''") + "'";
    }

    /**
     * Expand ~ to user home directory
     */
    public static String expandHome(String p) {
        if (p == null) return null;
        if (p.equals("~")) return System.getProperty("user.home");
        if (p.startsWith("~" + File.separator)) {
            return p.replaceFirst("~", System.getProperty("user.home"));
        }
        return p;
    }

    /**
     * Execute command safely in terminal widget (with clear to hide the command)
     */
    public static void exec(Project project, ShellTerminalWidget widget, String cmd) {
        try {
            // Run command and clear screen to hide the prompt
            widget.executeCommand("clear && " + cmd);
        } catch (IOException e) {
            notify(project, "Failed to start: " + e.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * Show notification to user
     */
    public static void notify(Project project, String msg, NotificationType type) {
        NotificationGroup group = NotificationGroupManager.getInstance().getNotificationGroup(NOTIF_GROUP_ID);
        Notification notification = (group != null)
                ? group.createNotification(msg, type)
                : new Notification(NOTIF_GROUP_ID, "Copilot CLI", msg, type);
        Notifications.Bus.notify(notification, project);
    }
}
