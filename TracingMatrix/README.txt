Программа создает матрицу трассировки из трех JSON файлов: TestCaseJson.json -- файл с данными о тест-кейсах,
Requirements.json -- файл с данными о требованиях, mappingSpecReq.json -- файл о перекрытии тест-кейсами требований.
Все эти файлы находятся в TracingMatrix\src\main\resources.

Для выполнения необходимо запустить Main.java в TracingMatrix\src\main\java

Результат программы - TracingMatrix.html, находится в папке TracingMatrix. Открыть файл нужно в Internet Explorer`е.

Чтобы изменить html код TracingMatrix перейдите в TracingMatrix\src\main\resources и отредактируйте файл sample,
не трогая тег <tr> c ключивым словом "pastereq" внутри и ключивое слово pastetc


