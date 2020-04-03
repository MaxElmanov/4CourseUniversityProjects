package files;

import commonUsefulFunctions.UsefulFunction;

import java.io.File;

public class FileExecutorForMatrixAdjacencyFactory
{
    public IFileExecutor getFileExecutor(File file)
    {
        //region Get file extension
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String fileExtension = fileName.substring(dotIndex + 1); //+1 because we need to get symbols after dot symbol(.)

        IFileExecutor fileExecutor = null;

        if (fileExtension.equalsIgnoreCase("txt"))
        {
            fileExecutor = new TXTFileExecutorForMatrixAdjacency(file);
        }
        else if (fileExtension.equalsIgnoreCase("xml"))
        {
            fileExecutor = new XMLFileExecutorForMatrixAdjacency(file);
        }
        else
        {
            UsefulFunction.throwException("Error: Check for some new file extension appearing except *.txt and *.xml");
        }

        return fileExecutor;
    }
}
