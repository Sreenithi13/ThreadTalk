public class QueueMessage {

    private String Senderusername;
    private String Message;
    private String recipientusername;

    //constructor
    public QueueMessage(String senderusername, String message , String recipientusername)
    {
         this.Senderusername = senderusername;
         this.Message = message;
         this.recipientusername = recipientusername;

    }

    //get username, message, recipient
    public String getSenderusername()
    {
        return Senderusername;
    }
    public String getMessage()
    {
        return Message;

    }

    public String getRecipientusername()
    {
        return recipientusername;
    }
}
