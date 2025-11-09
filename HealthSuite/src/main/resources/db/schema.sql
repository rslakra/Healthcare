-- HealthSuite db

-- users
create table if not exists `users` (
	username varchar(50) not null primary key,
	password varchar(50) not null,
	enabled boolean not null
);


-- authorities
create table if not exists `authorities` (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint FK_AUTHRITIES_USERS
	foreign key (username) references users(username)
);

create unique index if not exists IDX_USERS_AUTHORITIES on authorities (username, authority);


-- Insert users only if they don't exist
insert into `users` (`username`, `password`, `enabled`)
select * from (
	select 'rlakra' as username, 'password' as password, true as enabled
	union all
	select 'rslakra', 'secret', true
	union all
	select 'lakra', 'password', true
) as new_users
where not exists (select 1 from `users` where `users`.username = new_users.username);

-- Insert authorities only if they don't exist
insert into `authorities` (`username`, `authority`)
select * from (
	select 'rlakra' as username, 'ROLE_USER' as authority
	union all
	select 'rslakra', 'ROLE_USER'
	union all
	select 'rslakra', 'ROLE_ADMIN'
	union all
	select 'lakra', 'ROLE_USER'
) as new_authorities
where not exists (
	select 1 from `authorities` 
	where `authorities`.username = new_authorities.username 
	and `authorities`.authority = new_authorities.authority
);
