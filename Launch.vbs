' SocialApp Launcher - Double-click to run the app without console window
Set objShell = CreateObject("WScript.Shell")
Set objFSO = CreateObject("Scripting.FileSystemObject")

' Get the directory where this script is located
scriptDir = objFSO.GetParentFolderName(WScript.ScriptFullName)
batFile = objFSO.BuildPath(scriptDir, "SocialApp.bat")

' Check if the bat file exists
If Not objFSO.FileExists(batFile) Then
    MsgBox "Error: SocialApp.bat not found in " & scriptDir, vbCritical, "SocialApp - Error"
    WScript.Quit 1
End If

' Run the batch file in a minimized window (console hidden)
objShell.Run batFile, 0, False

WScript.Quit 0
