package repository;

import com.mygame.model.entity.Player;
import com.mygame.repository.BaseRepository;

public interface PlayerRepository extends BaseRepository<Player, Long> {

    @Override
    default Class<? super Player> getEntityClass() {
        return Player.class;
    }
}
