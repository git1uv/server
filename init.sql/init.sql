CREATE TABLE `mail` (
                        `mail_id`	bigint	NOT NULL,
                        `user_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                        `content`	varchar(255)	NOT NULL,
                        `chatbot_type`	varchar(255)	NOT NULL,
                        `created_at`	timestamp(6)	NOT NULL,
                        `is_read`	bool	NOT NULL	DEFAULT false,
                        `is_deleted`	bool	NOT NULL	DEFAULT false,
                        `deleted_at`	timestamp(6)	NULL
);

CREATE TABLE `mailbox` (
                           `mailbox_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                           `user_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                           `mail_id`	bigint	NOT NULL
);

CREATE TABLE `airplane` (
                            `airplane_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                            `receiver_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                            `sender_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                            `writer_name`	varchar(255)	NOT NULL,
                            `content`	varchar(255)	NULL,
                            `created_at`	timestamp(6)	NOT NULL,
                            `is_read`	bool	NOT NULL	DEFAULT false
);

CREATE TABLE `user` (
                        `user_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                        `password`	varchar(255)	NULL,
                        `nickname`	varchar(255)	NOT NULL,
                        `has_airplane`	bool	NOT NULL	DEFAULT false,
                        `chatbot`	varchar(255)	NULL	DEFAULT half,
                        `email`	varchar(255)	NOT NULL,
                        `login_type`	varchar(255)	NOT NULL	COMMENT 'general, kakao, google',
                        `mail_alert`	bool	NOT NULL	DEFAULT false,
                        `status`	bool	NOT NULL	DEFAULT true,
                        `inactive_date`	timestamp	NOT NULL
);

CREATE TABLE `chatbot_message` (
                                   `chatbot_message_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                                   `counseling_log_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                                   `user_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                                   `sender`	varchar(255)	NOT NULL	COMMENT 'USER, AI',
                                   `content`	varchar(255)	NOT NULL,
                                   `created_at`	timestamp(6)	NOT NULL,
                                   `emotion`	varchar(255)	NOT NULL	DEFAULT happy
);

CREATE TABLE `counseling_log` (
                                  `counseling_log_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                                  `user_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                                  `chatbot_type`	varchar(255)	NOT NULL,
                                  `started_at`	timestamp(6)	NOT NULL,
                                  `ended_at`	timestamp(6)	NOT NULL,
                                  `summary`	varchar(255)	NOT NULL,
                                  `suggestion`	varchar(255)	NOT NULL,
                                  `title`	varchar(255)	NOT NULL
);

CREATE TABLE `solution` (
                            `solution_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                            `counseling_log_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                            `content`	varchar(255)	NOT NULL,
                            `is_completed`	bool	NOT NULL	DEFAULT false
);

CREATE TABLE `calendars` (
                             `calendar_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                             `user_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                             `date`	date	NOT NULL,
                             `emotion`	varchar(255)	NULL,
                             `diary`	varchar(255)	NULL
);

CREATE TABLE `calendar_solution` (
                                     `calendar_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                                     `solution_id`	bigint	NOT NULL	COMMENT 'auto_increment'
);

CREATE TABLE `calendar_counseling_log` (
                                           `calendar_id`	bigint	NOT NULL	COMMENT 'auto_increment',
                                           `counseling_log_id`	bigint	NOT NULL	COMMENT 'auto_increment'
);

ALTER TABLE `mail` ADD CONSTRAINT `PK_MAIL` PRIMARY KEY (
                                                         `mail_id`
    );

ALTER TABLE `mailbox` ADD CONSTRAINT `PK_MAILBOX` PRIMARY KEY (
                                                               `mailbox_id`
    );

ALTER TABLE `airplane` ADD CONSTRAINT `PK_AIRPLANE` PRIMARY KEY (
                                                                 `airplane_id`
    );

ALTER TABLE `user` ADD CONSTRAINT `PK_USER` PRIMARY KEY (
                                                         `user_id`
    );

ALTER TABLE `chatbot_message` ADD CONSTRAINT `PK_CHATBOT_MESSAGE` PRIMARY KEY (
                                                                               `chatbot_message_id`
    );

ALTER TABLE `counseling_log` ADD CONSTRAINT `PK_COUNSELING_LOG` PRIMARY KEY (
                                                                             `counseling_log_id`
    );

ALTER TABLE `solution` ADD CONSTRAINT `PK_SOLUTION` PRIMARY KEY (
                                                                 `solution_id`
    );

ALTER TABLE `calendars` ADD CONSTRAINT `PK_CALENDARS` PRIMARY KEY (
                                                                   `calendar_id`
    );

ALTER TABLE `calendar_solution` ADD CONSTRAINT `PK_CALENDAR_SOLUTION` PRIMARY KEY (
                                                                                   `calendar_id`,
                                                                                   `solution_id`
    );

ALTER TABLE `calendar_counseling_log` ADD CONSTRAINT `PK_CALENDAR_COUNSELING_LOG` PRIMARY KEY (
                                                                                               `calendar_id`,
                                                                                               `counseling_log_id`
    );

ALTER TABLE `calendar_solution` ADD CONSTRAINT `FK_calendars_TO_calendar_solution_1` FOREIGN KEY (
                                                                                                  `calendar_id`
    )
    REFERENCES `calendars` (
                            `calendar_id`
        );

ALTER TABLE `calendar_solution` ADD CONSTRAINT `FK_solution_TO_calendar_solution_1` FOREIGN KEY (
                                                                                                 `solution_id`
    )
    REFERENCES `solution` (
                           `solution_id`
        );

ALTER TABLE `calendar_counseling_log` ADD CONSTRAINT `FK_calendars_TO_calendar_counseling_log_1` FOREIGN KEY (
                                                                                                              `calendar_id`
    )
    REFERENCES `calendars` (
                            `calendar_id`
        );

ALTER TABLE `calendar_counseling_log` ADD CONSTRAINT `FK_counseling_log_TO_calendar_counseling_log_1` FOREIGN KEY (
                                                                                                                   `counseling_log_id`
    )
    REFERENCES `counseling_log` (
                                 `counseling_log_id`
        );

