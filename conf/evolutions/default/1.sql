# --- !Ups
create table "article" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"title" VARCHAR NOT NULL,"body" VARCHAR NOT NULL);
create table "pinboard" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL);
create table "pinboard_article" ("pinboard_id" BIGINT NOT NULL,"article_id" BIGINT NOT NULL,"pinned_at" TIMESTAMP NOT NULL);
alter table "pinboard_article" add constraint "article_fk" foreign key("article_id") references "article"("id") on update NO ACTION on delete NO ACTION;
alter table "pinboard_article" add constraint "pinboard_fk" foreign key("pinboard_id") references "pinboard"("id") on update NO ACTION on delete NO ACTION;

insert into "article" ("title", "body") values ('hello','world'), ('good bye', 'world');

# --- !Downs
alter table "pinboard_article" drop constraint "article_fk";
alter table "pinboard_article" drop constraint "pinboard_fk";
drop table "pinboard_article";
drop table "pinboard";
drop table "article";
