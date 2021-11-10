-- Create table
create table WORK_FLOW_DEF
(
    process_key   VARCHAR2(255) not null,
    process_name  VARCHAR2(255),
    description   VARCHAR2(2000),
    version       VARCHAR2(255),
    category      VARCHAR2(255),
    create_time   DATE,
    update_time   DATE,
    deployment_id VARCHAR2(255),
    delete_flag   NUMBER
)
    tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table WORK_FLOW_DEF
  is '流程定义表';
-- Add comments to the columns
comment on column WORK_FLOW_DEF.process_key
  is '流程key';
comment on column WORK_FLOW_DEF.process_name
  is '流程名称';
comment on column WORK_FLOW_DEF.description
  is '流程描述';
comment on column WORK_FLOW_DEF.version
  is '版本号';
comment on column WORK_FLOW_DEF.category
  is '类型';
comment on column WORK_FLOW_DEF.create_time
  is '创建时间';
comment on column WORK_FLOW_DEF.update_time
  is '更新时间';
comment on column WORK_FLOW_DEF.deployment_id
  is '部署id';
comment on column WORK_FLOW_DEF.delete_flag
  is '删除标识,0:删除,1:未删除';
-- Create/Recreate primary, unique and foreign key constraints
alter table WORK_FLOW_DEF
    add constraint ACT_DEF_KEY primary key (PROCESS_KEY)
    using index
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;

-- Create table
create table WORK_FLOW_GE_XML
(
    deployment_id VARCHAR2(64) not null,
    xml           BLOB
)
    tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table WORK_FLOW_GE_XML
  is '记录流程的原始xml';
-- Add comments to the columns
comment on column WORK_FLOW_GE_XML.deployment_id
  is '部署id';
-- Create/Recreate primary, unique and foreign key constraints
alter table WORK_FLOW_GE_XML
    add constraint ACT_XML_DEPLOYMENT_ID primary key (DEPLOYMENT_ID)
    using index
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;
