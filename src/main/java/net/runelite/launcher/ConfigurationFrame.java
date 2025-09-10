package net.runelite.launcher;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConfigurationFrame extends JFrame
{
    private static final Color DARKER_GRAY_COLOR = new Color(30, 30, 30);

    private final JCheckBox chkboxDebug;
    private final JCheckBox chkboxNoDiffs;
    private final JCheckBox chkboxSkipTlsVerification;
    private final JCheckBox chkboxNoUpdates;
    private final JCheckBox chkboxSafemode;
    private final JCheckBox chkboxIpv4;
    private final JCheckBox chkboxRl;
    private final JCheckBox chkBoxSkipClientUpdateCheck;
    private final JTextField txtProxy;
    private final JTextField txtMaxMem;
    private final JTextField txtScale;
    private final JTextArea txtClientArguments;
    private final JTextArea txtJvmArguments;
    private final JComboBox<HardwareAccelerationMode> comboHardwareAccelMode;
    private final JComboBox<LaunchMode> comboLaunchMode;

    private ConfigurationFrame(LauncherSettings settings, KrakenPersistentSettings krakenSettings)
    {
        setTitle("RuneLite Launcher Configuration");

        BufferedImage iconImage;
        try (var in = ConfigurationFrame.class.getResourceAsStream(LauncherProperties.getRuneLite128()))
        {
            iconImage = ImageIO.read(in);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(iconImage);

        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBackground(DARKER_GRAY_COLOR);

        var topPanel = new JPanel();
        topPanel.setBackground(DARKER_GRAY_COLOR);
        topPanel.setLayout(new GridLayout(3, 2, 0, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        this.chkboxRl = this.addRuneLiteCheckbox(topPanel, krakenSettings);
        this.chkBoxSkipClientUpdateCheck = this.addSkipClientUpdateCheckCheckbox(topPanel, krakenSettings);

        topPanel.add(chkboxDebug = checkbox(
                "Debug",
                "Runs the launcher and client in debug mode. Debug mode writes debug level logging to the log files.",
                Boolean.TRUE.equals(settings.debug)
        ));

        topPanel.add(chkboxNoDiffs = checkbox(
                "Disable diffs",
                "Downloads full artifacts for updates instead of diffs.",
                Boolean.TRUE.equals(settings.nodiffs)
        ));

        topPanel.add(chkboxSkipTlsVerification = checkbox(
                "Disable TLS verification",
                "Disables TLS verification.",
                Boolean.TRUE.equals(settings.skipTlsVerification)
        ));

        topPanel.add(chkboxNoUpdates = checkbox(
                "Disable updates",
                "Disables the launcher self updating",
                Boolean.TRUE.equals(settings.noupdates)
        ));

        topPanel.add(chkboxSafemode = checkbox(
                "Safe mode",
                "Launches the client in safe mode",
                Boolean.TRUE.equals(settings.safemode)
        ));

        topPanel.add(chkboxIpv4 = checkbox(
                "IPv4",
                "Prefer IPv4 over IPv6",
                Boolean.TRUE.equals(settings.ipv4)
        ));

        pane.add(topPanel);

        var midPanel = new JPanel();
        midPanel.setBackground(DARKER_GRAY_COLOR);
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));

        // Client arguments section
        var clientArgsPanel = new JPanel();
        clientArgsPanel.setBackground(DARKER_GRAY_COLOR);
        clientArgsPanel.setLayout(new GridLayout(1, 2, 0, 0));
        clientArgsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        clientArgsPanel.add(label(
                "Client arguments",
                "Arguments passed to the client. One per line."
        ));
        var sp = new JScrollPane(txtClientArguments = area(Joiner.on('\n').join(settings.clientArguments)),
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        clientArgsPanel.add(sp);
        midPanel.add(clientArgsPanel);

        // JVM arguments section
        var jvmArgsPanel = new JPanel();
        jvmArgsPanel.setBackground(DARKER_GRAY_COLOR);
        jvmArgsPanel.setLayout(new GridLayout(1, 2, 0, 0));
        jvmArgsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        jvmArgsPanel.add(label(
                "JVM arguments",
                "Arguments passed to the JVM. One per line."
        ));
        sp = new JScrollPane(txtJvmArguments = area(Joiner.on('\n').join(settings.jvmArguments)),
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jvmArgsPanel.add(sp);
        midPanel.add(jvmArgsPanel);

        // Max Memory section
        var maxMemPanel = new JPanel();
        maxMemPanel.setBackground(DARKER_GRAY_COLOR);
        maxMemPanel.setLayout(new GridLayout(1, 2, 0, 0));
        maxMemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        this.txtMaxMem = this.addMaxMemText(maxMemPanel, krakenSettings);
        midPanel.add(maxMemPanel);

        // Proxy section
        var proxyPanel = new JPanel();
        proxyPanel.setBackground(DARKER_GRAY_COLOR);
        proxyPanel.setLayout(new GridLayout(1, 2, 0, 0));
        proxyPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        this.txtProxy = this.addProxyText(proxyPanel, krakenSettings);
        midPanel.add(proxyPanel);

        // Scale section
        var scalePanel = new JPanel();
        scalePanel.setBackground(DARKER_GRAY_COLOR);
        scalePanel.setLayout(new GridLayout(1, 2, 0, 0));
        scalePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        scalePanel.add(label(
                "Scale",
                "Scaling factor for Java 2D"
        ));
        scalePanel.add(txtScale = field(settings.scale != null ? Double.toString(settings.scale) : null));
        midPanel.add(scalePanel);

        // Hardware acceleration section
        var hwAccelPanel = new JPanel();
        hwAccelPanel.setBackground(DARKER_GRAY_COLOR);
        hwAccelPanel.setLayout(new GridLayout(1, 2, 0, 0));
        hwAccelPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        hwAccelPanel.add(label(
                "Hardware acceleration",
                "Hardware acceleration mode for Java 2D."
        ));
        hwAccelPanel.add(comboHardwareAccelMode = combobox(
                HardwareAccelerationMode.values(),
                settings.hardwareAccelerationMode
        ));
        midPanel.add(hwAccelPanel);

        // Launch mode section
        var launchModePanel = new JPanel();
        launchModePanel.setBackground(DARKER_GRAY_COLOR);
        launchModePanel.setLayout(new GridLayout(1, 2, 0, 0));
        launchModePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        launchModePanel.add(label("Launch mode", null));
        launchModePanel.add(comboLaunchMode = combobox(
                LaunchMode.values(),
                settings.launchMode
        ));
        midPanel.add(launchModePanel);

        pane.add(midPanel);

        var buttonPanel = new JPanel();
        buttonPanel.setBackground(DARKER_GRAY_COLOR);

        var save = new JButton("Save");
        save.addActionListener(this::save);
        buttonPanel.add(save);

        var cancel = new JButton("Cancel");
        cancel.addActionListener(l -> dispose());
        buttonPanel.add(cancel);

        pane.add(buttonPanel);

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
    }

    private void save(ActionEvent l)
    {
        var settings = LauncherSettings.loadSettings();
        settings.debug = chkboxDebug.isSelected();
        settings.nodiffs = chkboxNoDiffs.isSelected();
        settings.skipTlsVerification = chkboxSkipTlsVerification.isSelected();
        settings.noupdates = chkboxNoUpdates.isSelected();
        settings.safemode = chkboxSafemode.isSelected();
        settings.ipv4 = chkboxIpv4.isSelected();

        var t = txtScale.getText();
        settings.scale = null;
        if (!t.isEmpty())
        {
            try
            {
                settings.scale = Double.parseDouble(t);
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        settings.clientArguments = Splitter.on('\n')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(txtClientArguments.getText());

        settings.jvmArguments = Splitter.on('\n')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(txtJvmArguments.getText());

        settings.hardwareAccelerationMode = (HardwareAccelerationMode) comboHardwareAccelMode.getSelectedItem();
        settings.launchMode = (LaunchMode) comboLaunchMode.getSelectedItem();

        LauncherSettings.saveSettings(settings);
        this.applyKrakenSettings();

        // IPv4 change requires patching packr config
        PackrConfig.patch(config ->
        {
            List<String> vmArgs = (List) config.computeIfAbsent("vmArgs", k -> new ArrayList<>());
            if (settings.ipv4)
            {
                vmArgs.add("-Djava.net.preferIPv4Stack=true");
            }
            else
            {
                vmArgs.remove("-Djava.net.preferIPv4Stack=true");
            }
        });

        log.info("Updated launcher configuration:" + System.lineSeparator() + "{}", settings.configurationStr());

        dispose();
    }

    private static JLabel label(String name, String tooltip)
    {
        var label = new JLabel(name);
        label.setToolTipText(tooltip);
        label.setForeground(Color.WHITE);
        return label;
    }

    private static JTextField field(@Nullable String value)
    {
        return new JTextField(value);
    }

    private static JTextArea area(@Nullable String value)
    {
        return new JTextArea(value, 2, 20);
    }

    private static JCheckBox checkbox(String name, String tooltip, boolean checked)
    {
        var checkbox = new JCheckBox(name);
        checkbox.setSelected(checked);
        checkbox.setToolTipText(tooltip);
        checkbox.setForeground(Color.WHITE);
        checkbox.setBackground(DARKER_GRAY_COLOR);
        return checkbox;
    }

    private JCheckBox addRuneLiteCheckbox(JPanel topPanel, KrakenPersistentSettings settings) {
        JCheckBox box = checkbox("RuneLite mode", "Excludes all the Kraken additions", Boolean.TRUE.equals(settings.rlMode));
        topPanel.add(box);
        return box;
    }

    private JCheckBox addSkipClientUpdateCheckCheckbox(JPanel topPanel, KrakenPersistentSettings settings) {
        JCheckBox box = checkbox("Skip RuneLite Update check", "Skips security checks when new RuneLite clients are released.", Boolean.TRUE.equals(settings.skipUpdatedClientCheck));
        topPanel.add(box);
        return box;
    }

    private static <E> JComboBox<E> combobox(E[] values, E default_)
    {
        var combobox = new JComboBox<>(values);
        combobox.setSelectedItem(default_);
        return combobox;
    }

    static void open()
    {
        new ConfigurationFrame(LauncherSettings.loadSettings(), KrakenPersistentSettings.loadSettings())
                .setVisible(true);
    }

    private JTextField addProxyText(JPanel panel, KrakenPersistentSettings settings) {
        panel.add(label("Proxy", "Proxy to load the client with."));
        JTextField textField = field(settings.proxy);
        panel.add(textField);
        return textField;
    }

    private JTextField addMaxMemText(JPanel panel, KrakenPersistentSettings settings) {
        panel.add(label("Max Memory", "Amount of memory to load the client with"));
        JTextField textField = field(settings.maxMem);
        panel.add(textField);
        return textField;
    }

    private void applyKrakenSettings() {
        KrakenPersistentSettings settings = KrakenPersistentSettings.loadSettings();
        settings.skipUpdatedClientCheck = this.chkBoxSkipClientUpdateCheck.isSelected();
        settings.rlMode = this.chkboxRl.isSelected();
        settings.proxy = this.txtProxy.getText();
        settings.maxMem = this.txtMaxMem.getText();
        KrakenPersistentSettings.saveSettings(settings);
    }

    public static void main(String[] args)
    {
        open();
    }
}