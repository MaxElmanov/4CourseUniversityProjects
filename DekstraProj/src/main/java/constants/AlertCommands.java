package constants;

public enum AlertCommands
{
    RIGHTS_RESULT("(INFORMATION) Command successfully completed."),
    WARNING_RESULT("(WARNING) Command may completed unsuccessfully."),
    ERROR_RESULT("(ERROR) Wrong command completed.");
    
    private String command;

    AlertCommands(String command)
    {
        this.command = command;
    }

    public String getCommand()
    {
        return command;
    }
}
