[Setup]
AppName=RuneLite Launcher
AppPublisher=RuneLite
UninstallDisplayName=RuneLite
AppVersion=${project.version}
AppSupportURL=https://runelite.net/
DefaultDirName={localappdata}\RuneLite

; ~30 mb for the repo the launcher downloads
ExtraDiskSpaceRequired=30000000
ArchitecturesAllowed=arm64
PrivilegesRequired=lowest

WizardSmallImageFile=${project.projectDir}/innosetup/runelite_small.bmp
SetupIconFile=${project.projectDir}/innosetup/runelite.ico
UninstallDisplayIcon={app}\RuneLite.exe

Compression=lzma2
SolidCompression=yes

OutputDir=${project.projectDir}
OutputBaseFilename=RuneLiteSetupAArch64

[Tasks]
Name: DesktopIcon; Description: "Create a &desktop icon";

[Files]
Source: "${project.projectDir}\build\win-aarch64\RuneLite.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "${project.projectDir}\build\win-aarch64\RuneLite.jar"; DestDir: "{app}"
Source: "${project.projectDir}\build\win-aarch64\launcher_aarch64.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "${project.projectDir}\build\win-aarch64\config.json"; DestDir: "{app}"
Source: "${project.projectDir}\build\win-aarch64\jre\*"; DestDir: "{app}\jre"; Flags: recursesubdirs

[Icons]
; start menu
Name: "{userprograms}\RuneLite\RuneLite"; Filename: "{app}\RuneLite.exe"
Name: "{userprograms}\RuneLite\RuneLite (configure)"; Filename: "{app}\RuneLite.exe"; Parameters: "--configure"
Name: "{userprograms}\RuneLite\RuneLite (safe mode)"; Filename: "{app}\RuneLite.exe"; Parameters: "--safe-mode"
Name: "{userdesktop}\RuneLite"; Filename: "{app}\RuneLite.exe"; Tasks: DesktopIcon

[Run]
Filename: "{app}\RuneLite.exe"; Parameters: "--postinstall"; Flags: nowait
Filename: "{app}\RuneLite.exe"; Description: "&Open RuneLite"; Flags: postinstall skipifsilent nowait

[InstallDelete]
; Delete the old jvm so it doesn't try to load old stuff with the new vm and crash
Type: filesandordirs; Name: "{app}\jre"
; previous shortcut
Type: files; Name: "{userprograms}\RuneLite.lnk"

[UninstallDelete]
Type: filesandordirs; Name: "{%USERPROFILE}\.runelite\repository2"
; includes install_id, settings, etc
Type: filesandordirs; Name: "{app}"

[Code]
#include "upgrade.pas"
#include "usernamecheck.pas"
#include "dircheck.pas"