[Setup]
AppName=Kraken Launcher
AppPublisher=Kraken
UninstallDisplayName=Kraken
AppVersion=${project.version}
AppSupportURL=https://kraken-plugins.duckdns.org/support
DefaultDirName={localappdata}\RuneLite

; ~30 mb for the repo the launcher downloads
ExtraDiskSpaceRequired=30000000
ArchitecturesAllowed=x64
PrivilegesRequired=lowest

WizardSmallImageFile=${project.projectDir}\innosetup\kraken_small.bmp
SetupIconFile=${project.projectDir}\innosetup\kraken.ico
UninstallDisplayIcon={app}\RuneLite.exe

Compression=lzma2
SolidCompression=yes

OutputDir=${project.projectDir}
OutputBaseFilename=KrakenSetup

[Tasks]
Name: DesktopIcon; Description: "Create a &desktop icon";

[Files]
; only install kraken (RuneLite) .jar file. Dont mess with any RuneLite installs
Source: "${project.projectDir}\build\libs\RuneLite.jar"; DestDir: "{app}"

[InstallDelete]
; previous shortcut
Type: files; Name: "{app}\RuneLite.jar"

[UninstallDelete]
Type: files; Name: "{app}\RuneLite.jar"
