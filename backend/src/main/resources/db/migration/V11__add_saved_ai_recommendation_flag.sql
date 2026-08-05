alter table ai_recommendations
    add column if not exists saved boolean not null default false;
