/**
 *
 * @author Maruf
 */
import java.time.Instant ;
import java.time.Duration ;
import java.time.temporal.ChronoUnit;

public class RateLimitedCalculator {
       
    private final int callLimit ; // Request Limit will be initialized by the constructor
    private int callCount  ; // Current call count 
    private final Duration resetTime ; // Amount of time after the Limiter will be reset 
    private Instant lastReset;
    private final long resetTimeUserDefinedMinutes = 1 ;
    public RateLimitedCalculator(int intendedLimit)
            {
                   this.callLimit = intendedLimit ;
                   this.callCount = 0 ; // Default call count is 0 
                   this.resetTime = Duration.ofMinutes(resetTimeUserDefinedMinutes);  
                   // Use it for Debugging  
                   
                   //this.resetTime = Duration.ofMinutes(1); 
                                                           /* 
                                                              The Value is set for per minute requests.
                                                              Duration Set . This method can be 
                                                              used for days , hours , minutes as well .
                                                           */
                                                           
                   this.lastReset = Instant.now() ; /* 
                                                       Sets the current time as last reset time 
                                                       after resetting .
                                                    */
                  
                   
            }
    public void rateLimitResetter()
        {
          //System.out.println(" Reset "); // Use to Debug whether the Requests are resetted or not 
            Instant currentTime = Instant.now(); 
            
          //  System.out.print(currentTime+"       "+lastReset); // Check the Time Difference
            if(Duration.between(lastReset,currentTime).compareTo(resetTime)>=0)
                {
                    callCount = 0 ;
                    lastReset = currentTime ;
                }
        }
    public int getSum(int a , int b) throws rateLimitException, InterruptedException
        {
            rateLimitResetter() ;
            if(callCount >= callLimit)
                {
                    Instant currentTime = Instant.now();
                    
                    long elapsedTime = ChronoUnit.SECONDS.between(lastReset, currentTime) ;
                    long remainingTime = resetTimeUserDefinedMinutes*60 - elapsedTime ;
                    throw new rateLimitException(" Your request limit has exceeded or a problem occured ! "
                            + "Try again later after "
                            + remainingTime
                            + " Seconds "
                    );                    
                }
            
            Thread.sleep(100); /* Slows Down to Check Output .
                                Use this Function to check when the requests are processed .
                               */
            callCount++ ; // Function Access is Granted , so add 1 .
            
            return a+b ;
        }
    
    public static void main(String args[])
                {
                    RateLimitedCalculator request = new RateLimitedCalculator(5) ; // Insert the Call Limit to be processed
                    
                    
                        for(int i= 0 ; i<10000;i++)   // A huge number of requests .
                        {
                            try{
                            System.out.println("The Sum is : " + request.getSum(10,1));
                            }
                            catch(InterruptedException | rateLimitException e )   
                            {
                            System.out.println(e) ;
                            }
                        }
                    
                }
}

class rateLimitException extends Exception{
    public rateLimitException(String alertMessage)
    {
        super(alertMessage) ;
    }
}