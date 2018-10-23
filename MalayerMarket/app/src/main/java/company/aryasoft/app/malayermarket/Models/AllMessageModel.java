package company.aryasoft.app.malayermarket.Models;

/**
 * Created by MohamadAmin on 2/25/2018.
 */

public class AllMessageModel
{
    private String Topic;
    private String Date;
    private int Status;

    public AllMessageModel(String topic, String date, int status)
    {
        Topic = topic;
        Date = date;
        Status = status;
    }

    public String getTopic()
    {
        return Topic;
    }

    public String getDate()
    {
        return Date;
    }

    public int getStatus()
    {
        return Status;
    }

    public void setTopic(String topic)
    {
        Topic = topic;
    }

    public void setDate(String date)
    {
        Date = date;
    }

    public void setStatus(int status)
    {
        Status = status;
    }
}
