package ZombieSkills;

public interface ISkill {
    public boolean trigger();

    public void action();

    public void disable();

    public void enable();
}
