package constants;

public enum AlertCommands
{
    RIGHTS_RESULT("(INFORMATION) Right execution. command successfully completed"),
    WARNING_RESULT("(WARNING) Command may completed unsuccessfully"),
    ERROR_RESULT("(ERROR) Wrong command completion");
    
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
