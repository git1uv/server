CREATE TABLE `mail` (
                        `mail_id`	bigint	NOT NULL AUTO_INCREMENT,
                        `user_id`	bigint	NOT NULL,
                        `content`	varchar(255)	NOT NULL,
                        `chatbot_type`	varchar(255)	NOT NULL,
                        `created_at`	timestamp(6)	NOT NULL,
                        `is_read`	bool	NOT NULL	DEFAULT false,
                        `is_deleted`	bool	NOT NULL	DEFAULT false,
                        `deleted_at`	timestamp(6)	NULL,
                        PRIMARY KEY (`mail_id`)
);

CREATE TABLE `mailbox` (
                           `mailbox_id`	bigint	NOT NULL AUTO_INCREMENT,
                           `user_id`	bigint	NOT NULL,
                           `mail_id`	bigint	NOT NULL,
                           PRIMARY KEY (`mailbox_id`)
);

CREATE TABLE `airplane` (
                            `airplane_id`	bigint	NOT NULL AUTO_INCREMENT,
                            `receiver_id`	bigint	NOT NULL,
                            `sender_id`	bigint	NOT NULL,
                            `writer_name`	varchar(255)	NOT NULL,
                            `content`	varchar(255)	NULL,
                            `created_at`	timestamp(6)	NOT NULL,
                            `is_read`	bool	NOT NULL	DEFAULT false,
                            PRIMARY KEY (`airplane_id`)
);

CREATE TABLE `user` (
                        `user_id`	bigint	NOT NULL AUTO_INCREMENT,
                        `password`	varchar(255)	NULL,
                        `nickname`	varchar(255)	NOT NULL,
                        `has_airplane`	bool	NOT NULL	DEFAULT false,
                        `chatbot`	varchar(255)	NULL	DEFAULT 'half',
                        `email`	varchar(255)	NOT NULL,
                        `login_type`	varchar(255)	NOT NULL	COMMENT 'general, kakao, google',
                        `mail_alert`	bool	NOT NULL	DEFAULT false,
                        `status`	bool	NOT NULL	DEFAULT true,
                        `inactive_date`	timestamp	NOT NULL,
                        PRIMARY KEY (`user_id`)
);

CREATE TABLE `chatbot_message` (
                                   `chatbot_message_id`	bigint	NOT NULL AUTO_INCREMENT,
                                   `counseling_log_id`	bigint	NOT NULL,
                                   `user_id`	bigint	NOT NULL,
                                   `sender`	varchar(255)	NOT NULL	COMMENT 'USER, AI',
                                   `content`	varchar(255)	NOT NULL,
                                   `created_at`	timestamp(6)	NOT NULL,
                                   `emotion`	varchar(255)	NOT NULL	DEFAULT 'happy',
                                   PRIMARY KEY (`chatbot_message_id`)
);

CREATE TABLE `counseling_log` (
                                  `counseling_log_id`	bigint	NOT NULL AUTO_INCREMENT,
                                  `user_id`	bigint	NOT NULL,
                                  `chatbot_type`	varchar(255)	NOT NULL,
                                  `started_at`	timestamp(6)	NOT NULL,
                                  `ended_at`	timestamp(6)	NOT NULL,
                                  `summary`	varchar(255)	NOT NULL,
                                  `suggestion`	varchar(255)	NOT NULL,
                                  `title`	varchar(255)	NOT NULL,
                                  PRIMARY KEY (`counseling_log_id`)
);

CREATE TABLE `solution` (
                            `solution_id`	bigint	NOT NULL AUTO_INCREMENT,
                            `counseling_log_id`	bigint	NOT NULL,
                            `content`	varchar(255)	NOT NULL,
                            `is_completed`	bool	NOT NULL	DEFAULT false,
                            PRIMARY KEY (`solution_id`)
);

CREATE TABLE `calendars` (
                             `calendar_id`	bigint	NOT NULL AUTO_INCREMENT,
                             `user_id`	bigint	NOT NULL,
                             `date`	date	NOT NULL,
                             `emotion`	varchar(255)	NULL,
                             `diary`	varchar(255)	NULL,
                             PRIMARY KEY (`calendar_id`)
);

CREATE TABLE `calendar_solution` (
                                     `calendar_id`	bigint	NOT NULL,
                                     `solution_id`	bigint	NOT NULL,
                                     PRIMARY KEY (`calendar_id`, `solution_id`),
                                     FOREIGN KEY (`calendar_id`) REFERENCES `calendars` (`calendar_id`),
                                     FOREIGN KEY (`solution_id`) REFERENCES `solution` (`solution_id`)
);

CREATE TABLE `calendar_counseling_log` (
                                           `calendar_id` bigint NOT NULL,
                                           `counseling_log_id` bigint NOT NULL,
                                           PRIMARY KEY (`calendar_id`, `counseling_log_id`),
                                           FOREIGN KEY (`calendar_id`) REFERENCES `calendars` (`calendar_id`),
                                           FOREIGN KEY (`counseling_log_id`) REFERENCES `counseling_log` (`counseling_log_id`)
);

