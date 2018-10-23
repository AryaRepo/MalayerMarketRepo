package company.aryasoft.app.malayermarket.Models;

/**
 * Created by MohamadAmin on 2/26/2018.
 */

public class MessageModel
{
    private String text;
    private int status;
    private String date;
    private int type;  // 0=received   1=sent


    public MessageModel(String text, int status, String date,int type)
    {
        this.text = text;
        this.status = status;
        this.date = date;
        this.type=type;
    }

    public String getText()
    {
        return text;
    }

    public int getStatus()
    {
        return status;
    }

    public String getDate()
    {
        return date;
    }

    public int getType()
    {
        return type;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
