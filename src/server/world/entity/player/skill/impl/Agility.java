package server.world.entity.player.skill.impl;

import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;

public class Agility extends TrainableSkill {

    // FIXME: Clearly haven't started this.

    @Override
    public int index() {
        return SkillManager.AGILITY;
    }

    @Override
    public void reset(Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public Skill skill() {
        return Skill.AGILITY;
    }
}
