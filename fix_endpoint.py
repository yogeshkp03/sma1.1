with open(r'D:\sma\sma_app\lib\core\constants\api_constants.dart', 'r') as f:
    content = f.read()

old = "static const String recommendations = '/recommendations';"
new = "static const String recommendations = '/v1/recommendations';"

content = content.replace(old, new)

with open(r'D:\sma\sma_app\lib\core\constants\api_constants.dart', 'w') as f:
    f.write(content)

print('Done')
