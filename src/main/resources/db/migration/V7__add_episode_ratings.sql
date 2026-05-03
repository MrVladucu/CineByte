CREATE TABLE episode_ratings (
    user_id       UUID     NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    show_id       BIGINT   NOT NULL,
    season_number INTEGER  NOT NULL,
    episode_number INTEGER NOT NULL,
    episode_id    BIGINT   NOT NULL,
    rating        SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 10),
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, episode_id)
);
