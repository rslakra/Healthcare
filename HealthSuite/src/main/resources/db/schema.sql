-- ============================================================================
-- HealthSuite Database Schema
-- ============================================================================

-- ============================================================================
-- ROLES TABLE
-- ============================================================================
-- Application roles for healthcare team members
create table if not exists `roles` (
	id bigint auto_increment primary key,
	name varchar(64) not null unique,
	description varchar(255)
);

-- ============================================================================
-- INSERT DEFAULT ROLES
-- ============================================================================
-- Application roles
insert into `roles` (`name`, `description`)
select * from (
	select 'ADMIN' as name, 'Administrator role with full system access' as description
	union all
	select 'USER', 'Standard user role with basic access'
	union all
	select 'NURSE', 'Nurse role for healthcare professionals'
	union all
	select 'PHYSICIAN', 'Physician role for medical doctors'
	union all
	select 'ASSISTANT', 'Assistant role for support staff'
) as new_roles
where not exists (select 1 from `roles` where `roles`.name = new_roles.name);

-- ============================================================================
-- USERS TABLE
-- ============================================================================
-- User accounts for authentication and profile information
create table if not exists `users` (
	id bigint auto_increment primary key,
	username varchar(64) not null unique,
	email varchar(128) not null unique,
	first_name varchar(64),
	last_name varchar(64),
	password varchar(128) not null,
	enabled boolean not null default true,
	created_at timestamp default current_timestamp,
	updated_at timestamp default current_timestamp on update current_timestamp
);

-- ============================================================================
-- USER ROLES TABLE
-- ============================================================================
-- Many-to-many relationship between users and roles
create table if not exists `user_roles` (
	user_id bigint not null,
	role_id bigint not null,
	primary key (user_id, role_id),
	constraint FK_USER_ROLES_USERS
		foreign key (user_id) references users(id) on delete cascade,
	constraint FK_USER_ROLES_ROLES
		foreign key (role_id) references roles(id) on delete cascade
);

create unique index if not exists IDX_USER_ROLES_USER_ID_ROLE_ID 
	on user_roles (user_id, role_id);

-- ============================================================================
-- NOTE: Spring Security authorities are derived from user_roles table
-- No separate authorities table needed - authorities are generated from roles
-- by joining user_roles and roles tables (see SecurityConfig.java)
-- ============================================================================

-- ============================================================================
-- GOALS TABLE
-- ============================================================================
-- Exercise goals set by users
create table if not exists `goals` (
	id bigint auto_increment primary key,
	user_id bigint not null,
	activity_type varchar(64) not null,
	minutes int not null,
	created_at timestamp default current_timestamp,
	updated_at timestamp default current_timestamp on update current_timestamp,
	constraint FK_GOALS_USERS
		foreign key (user_id) references users(id) on delete cascade
);

create index if not exists IDX_GOALS_USER_ID on goals (user_id);
create index if not exists IDX_GOALS_ACTIVITY_TYPE on goals (activity_type);

-- ============================================================================
-- EXERCISES TABLE
-- ============================================================================
-- Exercise records logged by users
create table if not exists `exercises` (
	id bigint auto_increment primary key,
	user_id bigint not null,
	activity_type varchar(64) not null,
	minutes int not null,
	description varchar(500),
	exercise_date date not null,
	created_at timestamp default current_timestamp,
	updated_at timestamp default current_timestamp on update current_timestamp,
	constraint FK_EXERCISES_USERS
		foreign key (user_id) references users(id) on delete cascade
);

create index if not exists IDX_EXERCISES_USER_ID on exercises (user_id);
create index if not exists IDX_EXERCISES_ACTIVITY_TYPE on exercises (activity_type);
create index if not exists IDX_EXERCISES_EXERCISE_DATE on exercises (exercise_date);



-- ============================================================================
-- INSERT DEFAULT USERS
-- ============================================================================
-- Default test users for development
insert into `users` (`username`, `email`, `password`, `enabled`, `first_name`, `last_name`)
select * from (
	select 'rlakra' as username, 'rlakra@example.com' as email, 'password' as password, true as enabled, 'R' as first_name, 'Lakra' as last_name
	union all
	select 'rslakra', 'rslakra@example.com', 'secret', true, 'RS' as first_name, 'Lakra' as last_name
	union all
	select 'lakra', 'lakra@example.com', 'password', true, 'L' as first_name, 'Lakra' as last_name
) as new_users
where not exists (select 1 from `users` where `users`.username = new_users.username);

-- ============================================================================
-- INSERT DEFAULT USER ROLES (Application Roles)
-- ============================================================================
-- Assign application roles to default users
-- All users get USER role for basic access, plus their specific roles
insert into `user_roles` (`user_id`, `role_id`)
select u.id, r.id
from users u
inner join roles r on (
	(u.username = 'rlakra' and r.name = 'USER')
	or (u.username = 'rlakra' and r.name = 'NURSE')
	or (u.username = 'rslakra' and r.name = 'USER')
	or (u.username = 'rslakra' and r.name = 'ADMIN')
	or (u.username = 'rslakra' and r.name = 'NURSE')
	or (u.username = 'lakra' and r.name = 'USER')
)
where not exists (
	select 1 from user_roles ur 
	where ur.user_id = u.id and ur.role_id = r.id
);

-- ============================================================================
-- NOTE: Spring Security authorities are automatically generated from user_roles
-- The SecurityConfig.java uses a custom query that joins user_roles and roles
-- tables and prefixes role names with "ROLE_" (e.g., "USER" becomes "ROLE_USER")
-- No need to insert into authorities table - it's derived from user_roles
-- ============================================================================
