package model;
import java.util.UUID;
public class User
{
    private String name;
    private String email;
    private String password;
    private UUID id;
    private Privacy privacy;

    public User(String name, String email, String password, Privacy privacy)
    {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.privacy = privacy;
    }


    public String getName()
        {
            return name;
        }

    public String getEmail()
        {
            return email;
        }

    public String getPassword()
        {
            return password;
        }

    public UUID getId()
        {
            return id;
        }

    public Privacy getPrivacy()
        {
            return privacy;
        }
    public void setName(String name)
        {
            this.name = name;
        }    
    public void setEmail(String email)
        {
            this.email = email;
        }
    public void setPassword(String password)
        {
            this.password = password;
        }
    public void setPrivacy(Privacy privacy)
        {
            this.privacy = privacy;
        }
        
}