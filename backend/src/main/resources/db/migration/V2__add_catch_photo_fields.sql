alter table catches
    add column photo_url varchar(1000),
    add column photo_thumbnail_url varchar(1000),
    add column photo_caption varchar(255);

create index idx_catches_released on catches (released);
create index idx_catches_photo_present on catches (id) where photo_url is not null;
