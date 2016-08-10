-- :name steps-all :? :*
-- :doc Get all steps by user
select * from steps
where user_id = :user-id

-- :name insert-step-count :! :n
-- :doc Insert a step count
insert into steps (user_id, steps) values
(:user-id, :steps)

-- :name total-step-count :? :1
-- :doc Get total of steps for user
select sum(steps) as total from steps
where user_id = :user-id
