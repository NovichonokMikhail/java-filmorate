# ER Диаграма проекта
![ER Диаграмма](Filmorate_ER_Diagram.png)

## Таблицы
### users - *таблица пользователей*
- user_id (первичный ключ): уникальное id по которму находится пользователь
- email: почта пользователя
- name: имя пользователя (может быть равно полю login)
- login: логин пользователя
- birthday: дата рождения

### users_friends - *таблица дружб между пользователями*
- sender_id: user_id отправителя (часть ключа)
- reciever_id: user_id получателя (часть ключа)
- status: отображает статус дружбы (подтвержденная или нет)

### user_likes - *таблица фильмов от пользователей*
- user_id: id пользователя лайкнушего фильм (часть ключа)
- film_id: id фильма которому поставлен лайк (часть ключа)

### films - *таблица фильмов*
- film_id: уникальный id фильма (первичный ключ)
- name: название
- description: описание
- release_date: дата выхода
- duration: длительность в мин
- genre_id: ключ жанра
- rating_id: ключ рейтинга

### film_genres - *таблица жанров фильмов (на случай нескольких жанров)*
- film_id: уникальный id фильма (часть ключа)
- genre_id: genre_id: уникальный id жанра (часть ключа)

### genres - *таблица жанров*
- genre_id: уникальный id жанра (первичный ключ)
- name: назавние жанра

### ratings - *таблица рейтингов фильмов*
- genre_id: уникальный id рейтинга (первичный ключ)
- name: назавние рейтинга

## Примеры запросов

### 10 самых популярных фильмов
 ```sql
SELECT f.film_id
   f.name AS film_name,
   COUNT(l.userId) AS likes_count
FROM films AS f
LEFT JOIN user_likes AS l ON f.film_id = l.film_id
GROUP BY f.film_id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

-- Найти общих друзей между user1 (например, ID=1) и user2 (например, ID=2)
 ```sql
SELECT 
    u.user_id,
    u.login,
    u.name,
    u.email
FROM 
    user_friends AS uf1
INNER JOIN 
    user_friends AS uf2 ON uf1.friend_id = uf2.friend_id
INNER JOIN 
    users AS u ON uf1.friend_id = u.user_id
WHERE 
    uf1.user_id = 1  -- ID первого пользователя
    AND uf2.user_id = 2  -- ID второго пользователя
    AND uf1.confirmed = TRUE  -- Только подтвержденные друзья (если важно)
    AND uf2.confirmed = TRUE;
