/**
 * @author Tohidul Islam
 * Description- In this game the player, who controls a spaceship, must survive as long as possible and while doing so, collect 
 * the purple orbs floating around in space. These orbs will give the player 10 exp each and a certain amount of exp will be needed
 * to level up to the next level, there are 5 levels in total with 1 being beginner and 5 being extreme in which the speed of the 
 * game is greatly increased, if hit by a fireball, the users speed will decrease by a set amount. While playing, the player 
 * has access to the laser(spacebar) which will blow up everything in the row the user fired it in including good and bad objects.
 * The good objects in the game are the purple orbs, score multipliers such as x2, x3, x4, and x5, the extra healths, and last but
 * not least the max ammo which will completely refill the users ammo count. The bad objects in the game are the fireballs and the 
 * mines. The mine can only be avoided, if touched or shot at with the laser it will blow up everything currently on the screen and 
 * cost the user 2 lives, sometimes this can be used to the players advantage. At levels 2 and 4 the pattern of the fireballs will
 * change to become more like walls that sometimes have openings, if there are no openings the user must create a path using
 * their laser. The laser is accessible from the start of the game but requires the consumption of 1 ammo after every use,
 * the ammo the user can hold at a given time is capped at 5 and cannot be refilled other than through the max ammo buff,
 * so the laser must be used wisely. Each action in the game such as gaining a point or leveling up comes with its own sound 
 * effect. The game music is a instrumental version of the opening from the anime "Angel Beats". The music is very loud so 
 * lowered volume is recommended.
 */
import javax.swing.JOptionPane;
import java.applet.*;

public class Game
{
  private Grid grid;
  private int userRow;
  private int msElapsed;
  private int timesGet;
  private int timesAvoid;
  private int speed;
  private int lvl;
  private int exp;
  private int ammo;
  private AudioClip song;
  private AudioClip lvlUp;
  private AudioClip laser;
  private AudioClip hit;
  private AudioClip points;
  private AudioClip refill;
  private AudioClip over;
  private AudioClip power;
  private AudioClip empty;
  private AudioClip explosion;
  private AudioClip life;
  
  public Game()
  {
    grid = new Grid(7, 12);
    userRow = 0;
    speed=500;
    lvl=1;
    exp=0;
    ammo=5;
    song = Applet.newAudioClip(this.getClass().getResource("Song.wav"));
    laser = Applet.newAudioClip(this.getClass().getResource("laser.wav"));
    over = Applet.newAudioClip(this.getClass().getResource("end.wav"));
    points = Applet.newAudioClip(this.getClass().getResource("gain.wav"));
    lvlUp = Applet.newAudioClip(this.getClass().getResource("up.wav"));
    power = Applet.newAudioClip(this.getClass().getResource("power.wav"));
    hit = Applet.newAudioClip(this.getClass().getResource("hit.wav"));
    refill = Applet.newAudioClip(this.getClass().getResource("reload.wav"));
    empty = Applet.newAudioClip(this.getClass().getResource("empty.wav"));
    explosion = Applet.newAudioClip(this.getClass().getResource("boom.wav"));
    life = Applet.newAudioClip(this.getClass().getResource("heal.wav"));
    msElapsed = 0;
    timesGet = 0;
    timesAvoid = 0;
    updateTitle();
    grid.setImage(new Location(userRow, 0), "user.gif");
  }
  
  public void play()
  {
    song.loop();
    for(int r=0;r<7;r++)
    {
        for(int c=0;c<12;c++)
            grid.setImage(new Location(r,c), "back.gif");
    }
    grid.setImage(new Location(userRow, 0), "user.gif");
    JOptionPane.showMessageDialog(grid, "      Galactic Conquest\n(Press enter to continue)");
    JOptionPane.showMessageDialog(grid, "Welcome to Galactic Conquest");
    JOptionPane.showMessageDialog(grid, "INSTRUCTIONS:\n Traverse the universe in your spaceship collecting COSMIC ORBS as you \ngo, each orb will get you exp which in turn will increase your level as well \nas the difficulty. Avoid obstacles and try to obtain any powerup that cross \nyour path");
    JOptionPane.showMessageDialog(grid, "BAD (avoid):\n1. Flaming Comets- Who would want to get hit by one?\n2. Mines- Dont touch or use TMSULD on these or it \nwill blow up everything currently on the screen and \ncost you 2 lives");
    JOptionPane.showMessageDialog(grid, "GOOD (get):\n1. COSMIC ORBS- Shiny purple orbs. Cool.\n2. Score Multipliers- Who doesnt want a higher score?\n3. Hearts- more lives means more orbs\n4. Max Ammo- TMSULD's best friend");
    JOptionPane.showMessageDialog(grid, "Remember, when all seems to be going wrong \nyou can always use your special ability: \nThe Mega Super Ultra Laser Of Doom, or TMSULD for short");
    JOptionPane.showMessageDialog(grid, "TMSULD can be activated by pressing the space bar \nBut beware, each use will use up one ammo");
    JOptionPane.showMessageDialog(grid, "Good Luck");
    while (!isGameOver())
    {
      for(int r=0;r<7;r++)
      {
        for(int c=0;c<12;c++)
        {
            if(grid.getImage(new Location(r,c))==null)
                grid.setImage(new Location(r,c), "back.gif");
        }
      }
      grid.pause(100);
      handleKeyPress();
      scrollRight();
      if (msElapsed % speed == 0)
      {
        scrollLeft();
        populateRightEdge();
      }
      updateTitle();
      msElapsed += 100;
    }
    song.stop();
    over.play();
    JOptionPane.showMessageDialog(grid, "               GAME OVER\n      You scored "+timesGet+" points!\n      You survived "+msElapsed/1000+" seconds!\n      You reached level "+lvl+"!");
  }
  
  public void handleKeyPress()
  {
      int key=grid.checkLastKeyPressed();
      if(key==38)
      {
          if(userRow!=0)
          {
              grid.setImage(new Location(userRow, 0), null);
              userRow-=1;
              handleCollision(new Location(userRow, 0));
              grid.setImage(new Location(userRow, 0), "user.gif");
          }
      }
      if(key==40)
      {
          if(userRow!=grid.getNumRows()-1)
          {
              grid.setImage(new Location(userRow, 0), null);
              userRow+=1;
              handleCollision(new Location(userRow, 0));
              grid.setImage(new Location(userRow, 0), "user.gif");
          }
      }
      if(key==32&&ammo>=1)
      {
          laser.play();
          grid.setImage(new Location(userRow,1), "flames.gif");
          ammo--;
      }
      else if(key==32&&ammo==0)
          empty.play();
  }
  
  public void populateRightEdge()
  {
      int ran1=(int)(Math.random()*7);
      int ran2=(int)(Math.random()*7);
      int ran3=(int)(Math.random()*25);
      int ran4=(int)(Math.random()*50);
      int bonus2=(int)(Math.random()*100);
      int bonus3=(int)(Math.random()*1000);
      int bonus4=(int)(Math.random()*1000);
      int bonus5=(int)(Math.random()*10000);
      int maxAmmo=(int)(Math.random()*100);
      if(grid.getImage(new Location(ran1,11))==null&&lvl!=2&&lvl!=4)
        grid.setImage(new Location(ran1, 11), "avoid.gif");
      if(grid.getImage(new Location(ran2,11))==null)
        grid.setImage(new Location(ran2, 11), "get.gif");
      if(grid.getImage(new Location(ran1,11))==null&&(lvl==2||lvl==4))
      {
          if(msElapsed%2000==0)
          {
              grid.setImage(new Location(0, 11), "avoid.gif");
              grid.setImage(new Location(1, 11), "avoid.gif");
              grid.setImage(new Location(2, 11), "avoid.gif");
              grid.setImage(new Location(3, 11), "avoid.gif");
              grid.setImage(new Location(4, 11), "avoid.gif");
              grid.setImage(new Location(5, 11), "avoid.gif");
              grid.setImage(new Location(6, 11), "avoid.gif");
              grid.setImage(new Location(ran1, 11), null);
              grid.setImage(new Location(ran2, 11), null);
          }
          else
              grid.setImage(new Location(ran1, 11), "avoid.gif");
      }
      if(bonus2==0)
        grid.setImage(new Location((int)(Math.random()*7), 11), "two.gif");
      if(bonus3==0)
        grid.setImage(new Location((int)(Math.random()*7), 11), "three.gif");
      if(bonus4==0)
        grid.setImage(new Location((int)(Math.random()*7), 11), "four.gif");
      if(bonus5==0)
        grid.setImage(new Location((int)(Math.random()*7), 11), "five.gif");
      if(maxAmmo==0)
          grid.setImage(new Location((int)(Math.random()*7), 11), "ammo.gif");
      if(ran3==0)
          grid.setImage(new Location((int)(Math.random()*7), 11), "health.gif");
      if(ran4==0)
          grid.setImage(new Location((int)(Math.random()*7), 11), "mine.gif");
  }
  
  public void scrollLeft()
  {
      String holder;
      for(int r=0;r<7;r++)
      {
          for(int c=0;c<12;c++)
          {
              if(grid.getImage(new Location(r,c))!=null&&grid.getImage(new Location(r,c))!="user.gif"&&grid.getImage(new Location(r,c))!="flames.gif")
              {
                  holder=grid.getImage(new Location(r,c));
                  grid.setImage(new Location(r,c), null);
                  if(c!=0&&grid.getImage(new Location(r,c-1))!="user.gif"&&grid.getImage(new Location(r,c-1))!="flames.gif")
                  {
                      grid.setImage(new Location(r,c-1), holder);
                      
                  }
                  else if(c!=0&&grid.getImage(new Location(r,c-1))=="user.gif")
                  {
                      grid.setImage(new Location(r,c-1), holder);
                      handleCollision(new Location(r,c-1));
                      grid.setImage(new Location(r,c-1), "user.gif");
                  }
              }
          }
      }
  }
  
  public void scrollRight()
  {
      for(int r=0;r<7;r++)
      {
          for(int c=0;c<12;c++)
          {
              if(grid.getImage(new Location(r,c))!=null&&grid.getImage(new Location(r,c))=="flames.gif")
              {
                  if(c!=11&&grid.getImage(new Location(r,c+1)).equals("mine.gif"))
                      handleCollision(new Location(r,c+1));
                  else if(c!=11)
                    grid.setImage(new Location(r,c+1), "flames.gif");
                  if(c==11&&msElapsed%700==0)
                      for(int i=1;i<12;i++)
                      {
                          grid.setImage(new Location(r,i), null);
                      }
              }
          }
      }
  }
  
  public void handleCollision(Location loc)
  {
      if(grid.getImage(loc)!=null&&grid.getImage(loc)!="user.gif")
      {
          if(grid.getImage(loc)=="get.gif")
          {
              points.play();
              timesGet++;
              exp+=10;
              if(exp==500)
              {
                  lvlUp.play();
                  lvl=2;
              }
              if(exp==750)
              {
                  lvlUp.play();
                  lvl=3;
              }
              if(exp==1000)
              {
                  lvlUp.play();
                  lvl=4;
              }
              if(exp==1500)
              {
                  lvlUp.play();
                  lvl=5;
              }
              speed-=50;
              if(lvl==1&&speed<500)
                  speed=500;
              else if(lvl==2&&speed<400)
                  speed=400;
              else if(lvl==3&&speed<300)
                  speed=300;
              else if(lvl==4&&speed<200)
                  speed=200;
              else if(lvl==5&&speed<100)
                  speed=100;
          }
          else if(grid.getImage(loc)=="avoid.gif")
          {
              hit.play();
              timesAvoid++;
              speed+=200;
              if(speed>500)
                  speed=500;
          }
          else if(grid.getImage(loc)=="two.gif")
          {
              power.play();
              timesGet*=2;
          }
          else if(grid.getImage(loc)=="three.gif")
          {
              power.play();
              timesGet*=3;
          }
          else if(grid.getImage(loc)=="four.gif")
          {
              power.play();
              timesGet*=4;
          }
          else if(grid.getImage(loc)=="five.gif")
          {
              power.play();
              timesGet*=5;
          }
          else if(grid.getImage(loc)=="ammo.gif")
          {
              refill.play();
              ammo=5;
          }
          else if(grid.getImage(loc)=="health.gif")
          {
              life.play();
              timesAvoid--;
          }
          else if(grid.getImage(loc)=="mine.gif")
          {
              explosion.play();
              hit.play();
              for(int r=0;r<7;r++)
              {
                  for(int c=0;c<12;c++)
                  {
                      if(grid.getImage(new Location(r,c))!="user.gif")
                      {
                          grid.setImage(new Location(r,c), "back.gif");
                      }
                  }
              }
              timesAvoid+=2;
              if(timesAvoid>5)
                  timesAvoid=5;
          }
      }
  }
  
  public int getScore()
  {
    return timesGet;
  }
  
  public void updateTitle()//Seconds Survived: "+(msElapsed/1000)+"
  {
    grid.setTitle("Score: " + getScore()+"            Ammo: "+ammo+"            Lives: "+(5-timesAvoid)+"            Level: "+lvl+"            Exp: "+exp+"            Seconds Survived: "+(msElapsed/1000));
  }
  
  public boolean isGameOver()
  {
    if(5-timesAvoid==0)
        return true;
    else
        return false;
  }
  
  public static void test()
  {
    Game game = new Game();
    game.play();
  }
  
  public static void main(String[] args)
  {
    test();
  }
}