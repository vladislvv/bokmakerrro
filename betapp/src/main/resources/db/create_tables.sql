-- Создаем таблицу пользователей
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    balance REAL DEFAULT 0.0
);

-- Создаем таблицу матчей
CREATE TABLE IF NOT EXISTS matches (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    team1 TEXT NOT NULL,
    team2 TEXT NOT NULL,
    odds1 REAL NOT NULL,
    odds2 REAL NOT NULL,
    odds_draw REAL NOT NULL,
    status TEXT DEFAULT 'upcoming',
    result TEXT,
    datetime TEXT NOT NULL
);

-- Создаем таблицу ставок
CREATE TABLE IF NOT EXISTS bets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    match_id INTEGER NOT NULL,
    bet_type TEXT NOT NULL,
    amount REAL NOT NULL,
    odds REAL NOT NULL,
    status TEXT DEFAULT 'pending',
    placed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (match_id) REFERENCES matches(id)
);