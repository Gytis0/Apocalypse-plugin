package ZombieSkills;

public interface Skill {
    public boolean trigger();

    public void action();

    public void disable();

    public void enable();
}
