# YASK Backend API Notes

## Общая информация

Backend построен на Supabase (PostgreSQL + Auth + RLS).

Основная логика реализована через:

* SQL таблицы
* RLS политики
* триггеры
* RPC-функции (PostgreSQL functions)

Клиент (Android) взаимодействует с backend через RPC.

---

## Аутентификация

Используется Supabase Auth.

После регистрации:

* создается запись в `auth.users`
* автоматически создается профиль в `public.profiles` (через trigger)

---

## Основные сущности

### Profiles

* id (uuid)
* username
* display_name
* avatar_url
* bio

---

### Polls

* id
* author_id
* question
* image_url
* status (active / closed / deleted)
* visibility (public / private)
* expires_at
* created_at

---

### Poll Options

* id
* poll_id
* option_text
* position

---

### Votes

* id
* poll_id
* option_id
* user_id

---

### Likes

* id
* poll_id
* user_id

---

### Comments

* id
* poll_id
* author_id
* content

---

### Follows

* follower_id
* following_id

---

### Notifications

* id
* user_id
* actor_id
* poll_id
* comment_id
* type (like / comment / follow)
* is_read
* created_at

---

## 🔧 RPC функции

---

### 1. get_feed

```sql
get_feed(p_user_id uuid, p_limit int, p_offset int)
```

 Возвращает:

* список опросов (JSON array)

 Особенности:

* только public + active
* сортировка по дате
* включает:

  * автора
  * варианты
  * голоса
  * лайки
  * комментарии
  * user_vote
  * liked_by_me

---

### 2. get_poll_details

```sql
get_poll_details(p_poll_id uuid, p_user_id uuid)
```

 Возвращает:

* полный объект опроса

📦 Структура:

* poll info
* author
* options (с процентами)
* total_votes
* likes_count
* comments_count
* user_vote
* liked_by_me

---

### 3. get_user_profile

```sql
get_user_profile(p_profile_id uuid, p_current_user_id uuid)
```

 Возвращает:

* профиль пользователя

📦 Включает:

* username
* display_name
* avatar
* bio
* polls_count
* followers_count
* following_count
* followed_by_me

---

### 4. get_user_polls

```sql
get_user_polls(p_profile_id uuid, p_current_user_id uuid, p_limit int, p_offset int)
```

 Возвращает:

* список опросов пользователя

---

### 5. get_notifications

```sql
get_notifications(p_user_id uuid, p_limit int, p_offset int)
```

 Возвращает:

* список уведомлений

 Типы:

* like
* comment
* follow

---

### 6. create_poll

```sql
create_poll(p_question text, p_options text[], ...)
```

 Особенности:

* автор определяется через auth.uid()
* создается:

  * poll
  * options

 Ограничения:

* 2–6 вариантов
* длина текста проверяется

---

## Бизнес-логика (триггеры)

---

### Уведомления

Автоматически создаются:

* при лайке → type = 'like'
* при комментарии → type = 'comment'
* при подписке → type = 'follow'

---

### Ограничения

* 1 голос на пользователя в poll
* нельзя голосовать за option из другого poll
* нельзя подписаться на себя
* уникальные лайки (1 пользователь = 1 лайк)

---

## Безопасность

Используется Row Level Security (RLS):

* пользователь может:

  * изменять только свои данные
  * голосовать от своего имени
  * лайкать от своего имени
  * подписываться от своего имени

---

## Использование на клиенте

Клиент НЕ должен:

* собирать данные из разных таблиц
* считать проценты
* делать сложные JOIN

Клиент просто вызывает RPC:

* get_feed → главная страница
* get_poll_details → страница опроса
* get_user_profile → профиль
* get_notifications → уведомления

---

## Итог

Backend реализует:

* полноценную модель соцсети
* серверную бизнес-логику
* безопасный доступ к данным
* удобный контракт для клиента

---
