with open(r'D:\sma\backend\src\main\java\com\smartmeal\config\SecurityConfig.java', 'r') as f:
    content = f.read()

old = """.requestMatchers("/api/recommendations/**").permitAll()"""
new = """.requestMatchers("/api/recommendations/**").permitAll()
                .requestMatchers("/api/v1/recommendations/**").permitAll()"""

content = content.replace(old, new)

with open(r'D:\sma\backend\src\main\java\com\smartmeal\config\SecurityConfig.java', 'w') as f:
    f.write(content)

print('Done')
