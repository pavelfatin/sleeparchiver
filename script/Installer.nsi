!include "MUI.nsh"
!include "FileAssociation.nsh"

!define tutorial "Tutorial.xmz"
!define registry "Software\${PRODUCT}"
!define uninstall "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}"
!define menu "$SMPROGRAMS\${PRODUCT}"
!define uninstaller "uninstall.exe"

Name "${TITLE}"
OutFile "${PATH_OUT}"
InstallDir "$PROGRAMFILES\${PRODUCT}"
InstallDirRegKey HKLM "${registry}" "Install_Dir"

!include "Version.nsh"

XPStyle on
RequestExecutionLevel admin
SetCompress force
SetCompressor /SOLID /FINAL lzma
BrandingText http://pavelfatin.com

Function .onInit
  UserInfo::GetAccountType
  IfErrors Proceed
  UserInfo::GetAccountType
  Pop $1
  StrCmp $1 "Admin" Proceed 0
  MessageBox MB_ICONSTOP "You must have administrative privileges to install the software!"
  Abort
  Proceed:
FunctionEnd

;!define MUI_ICON "${DIR_FILES}\${FILE_ICON}"
!define MUI_FINISHPAGE_RUN "$INSTDIR\${FILE_LAUNCHER}"
!define MUI_FINISHPAGE_RUN_PARAMETERS "$INSTDIR\${tutorial}"
!define MUI_COMPONENTSPAGE_NODESC

!insertmacro MUI_PAGE_LICENSE "${DIR_LICENSES}\${FILE_LICENSE}"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
  
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

!insertmacro MUI_LANGUAGE "English"


Section "!${PRODUCT} (required)"
  SectionIn RO

  SetOutPath "$INSTDIR"
  File "${DIR_BUILD}\${FILE_JAR}"
  File "${DIR_BUILD}\${FILE_LAUNCHER}"
  File "${DIR_PLATFORM}\rxtxSerial.dll"
  File "${DIR_FILES}\${tutorial}"
  File /oname=readme.txt "${DIR_FILES}\readme-windows.txt"
  
  SetOutPath "$INSTDIR\lib"
  File /x RXTXcomm.jar "${DIR_LIBS}\*.*"
  File "${DIR_PLATFORM}\RXTXcomm.jar"

  SetOutPath "$INSTDIR\license"
  File "${DIR_LICENSES}\*.*"

  ${registerExtension} "$INSTDIR\${FILE_LAUNCHER}" ".xmz" "SleepArchiver database"
  
  WriteRegStr HKLM "${registry}" "Install_Dir" "$INSTDIR"
  
  WriteRegStr HKLM "${uninstall}" "DisplayName" "${PRODUCT}"
  WriteRegStr HKLM "${uninstall}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "${uninstall}" "NoModify" 1
  WriteRegDWORD HKLM "${uninstall}" "NoRepair" 1
  
  WriteUninstaller "${uninstaller}"
SectionEnd


SectionGroup /e "Shortcuts" 

Section "Start Menu"
  SetShellVarContext all
  CreateDirectory "${menu}"
  CreateShortCut "${menu}\${PRODUCT}.lnk" "$INSTDIR\${FILE_LAUNCHER}"
  CreateShortCut "${menu}\Tutorial.lnk" "$INSTDIR\${tutorial}"
;  CreateShortCut "${menu}\Readme.lnk" "$INSTDIR\readme.txt"  
  WriteINIStr "${menu}\Web Site.url" "InternetShortcut" "URL" "${WEBSITE}"
  CreateShortCut "${menu}\Uninstall.lnk" "$INSTDIR\${uninstaller}"
SectionEnd

Section "Desktop"
  SetShellVarContext all
  CreateShortCut "$DESKTOP\${product}.lnk" "$INSTDIR\${FILE_LAUNCHER}"
SectionEnd

Section /o "Quick Launch"
  SetShellVarContext all
  CreateShortCut "$QUICKLAUNCH\${product}.lnk" "$INSTDIR\${FILE_LAUNCHER}"
SectionEnd

SectionGroupEnd


Section "Uninstall"
  ${unregisterExtension} ".xmz" "${PRODUCT} database"
  
  Delete "$INSTDIR\lib\*.*"
  RMDir "$INSTDIR\lib"
  
  Delete "$INSTDIR\license\*.*"
  RMDir "$INSTDIR\license"

  Delete "$INSTDIR\${FILE_JAR}"
  Delete "$INSTDIR\${FILE_LAUNCHER}"
  Delete "$INSTDIR\rxtxSerial.dll"
  Delete "$INSTDIR\${tutorial}"
  Delete "$INSTDIR\readme.txt"
  Delete "$INSTDIR\${uninstaller}"
  RMDir "$INSTDIR"
    
  SetShellVarContext all
  
  Delete "${menu}\*.*"
  RMDir "${menu}"
  
  Delete "$DESKTOP\${PRODUCT}.lnk"
  Delete "$QUICKLAUNCH\${PRODUCT}.lnk"
  
  DeleteRegKey HKLM "${registry}"
  DeleteRegKey HKLM "${uninstall}"
SectionEnd
