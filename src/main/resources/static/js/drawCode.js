var canvas = document.getElementById('myCanvas');
var ctx = canvas.getContext('2d');
ctx.font = '12px Arial';
ctx.textAlign = 'center';
ctx.textBaseline = 'middle';

ctx.fillText('Main', 190, 50);

ctx.beginPath();
ctx.ellipse(250, 75, 50, 25, 0, 0, Math.PI * 2);
ctx.stroke();

ctx.fillText('Главная', 250, 75);

ctx.beginPath();
ctx.moveTo(250, 100);
ctx.lineTo(250, 125);
ctx.stroke();

ctx.beginPath();
ctx.rect(200, 125, 100, 50);
ctx.stroke();

ctx.fillText('Integer x, y, z', 250, 150);

ctx.beginPath();
ctx.moveTo(210, 125);ctx.lineTo(210, 175);ctx.stroke();
ctx.beginPath();
ctx.moveTo(200, 135);ctx.lineTo(300, 135);ctx.stroke();
ctx.beginPath();
ctx.moveTo(250, 175);
ctx.lineTo(250, 200);
ctx.stroke();

ctx.beginPath();
ctx.rect(200, 200, 100, 50);
ctx.stroke();

ctx.fillText('x=5', 250, 225);

ctx.beginPath();
ctx.moveTo(250, 250);
ctx.lineTo(250, 275);
ctx.stroke();

ctx.beginPath();
ctx.moveTo(220, 275);
ctx.lineTo(300,275);
ctx.lineTo(280,325);
ctx.lineTo(200,325);
ctx.closePath()
ctx.stroke();

ctx.fillText('Вывод x', 250, 300);

ctx.beginPath();
ctx.moveTo(250, 325);
ctx.lineTo(250, 350);
ctx.stroke();

ctx.beginPath();
ctx.rect(200, 350, 100, 50);
ctx.stroke();

ctx.fillText('y=10', 250, 375);

ctx.beginPath();
ctx.moveTo(250, 400);
ctx.lineTo(250, 425);
ctx.stroke();

ctx.beginPath();
ctx.rect(200, 425, 100, 50);
ctx.stroke();

ctx.fillText('z=sumInt(x, y)', 250, 450);

ctx.beginPath();
ctx.moveTo(250, 475);
ctx.lineTo(250, 500);
ctx.stroke();

ctx.beginPath();
ctx.moveTo(220, 500);
ctx.lineTo(300,500);
ctx.lineTo(280,550);
ctx.lineTo(200,550);
ctx.closePath()
ctx.stroke();

ctx.fillText('Вывод z', 250, 525);

ctx.beginPath();
ctx.moveTo(250, 550);
ctx.lineTo(250, 575);
ctx.stroke();

ctx.beginPath();
ctx.moveTo(250, 575);
ctx.lineTo(300,600);
ctx.lineTo(250,625);
ctx.lineTo(200,600);
ctx.closePath()
ctx.stroke();

ctx.fillText('x != y', 250, 600);

ctx.beginPath();
ctx.moveTo(300, 600);
ctx.lineTo(350, 600);
ctx.lineTo(350, 650);
ctx.stroke();
ctx.fillText('Да', 310,590);

ctx.beginPath();
ctx.moveTo(320, 650);
ctx.lineTo(400,650);
ctx.lineTo(380,700);
ctx.lineTo(300,700);
ctx.closePath()
ctx.stroke();

ctx.fillText('Вывод "you not loh"', 350, 675);

ctx.beginPath();
ctx.moveTo(350, 700);
ctx.lineTo(350, 725);
ctx.stroke();

ctx.beginPath();
ctx.moveTo(350, 725);
ctx.lineTo(250, 725);
ctx.stroke();

ctx.beginPath();
ctx.moveTo(200, 600);
ctx.lineTo(150, 600);
ctx.lineTo(150, 650);
ctx.stroke();
ctx.fillText('нет', 190,590);

ctx.beginPath();
ctx.moveTo(120, 650);
ctx.lineTo(200,650);
ctx.lineTo(180,700);
ctx.lineTo(100,700);
ctx.closePath()
ctx.stroke();

ctx.fillText('Вывод "Ты не лох"', 150, 675);

ctx.beginPath();
ctx.moveTo(150, 700);
ctx.lineTo(150, 725);
ctx.stroke();

ctx.beginPath();
ctx.moveTo(150, 725);
ctx.lineTo(250, 725);
ctx.stroke();

ctx.beginPath();
ctx.moveTo(250, 725);
ctx.lineTo(250, 750);
ctx.stroke();

ctx.beginPath();
ctx.ellipse(250, 775, 50, 25, 0, 0, Math.PI * 2);
ctx.stroke();

ctx.fillText('Конец', 250, 775);
ctx.fillText('sumInt', 390, 50);

ctx.beginPath();
ctx.ellipse(450, 75, 50, 25, 0, 0, Math.PI * 2);
ctx.stroke();

ctx.fillText('Главная', 450, 75);

ctx.beginPath();
ctx.moveTo(450, 100);
ctx.lineTo(450, 125);
ctx.stroke();

ctx.beginPath();
ctx.rect(400, 125, 100, 50);
ctx.stroke();

ctx.fillText('Integer sum', 450, 150);

ctx.beginPath();
ctx.moveTo(410, 125);ctx.lineTo(410, 175);ctx.stroke();
ctx.beginPath();
ctx.moveTo(400, 135);ctx.lineTo(500, 135);ctx.stroke();
ctx.beginPath();
ctx.moveTo(450, 175);
ctx.lineTo(450, 200);
ctx.stroke();

ctx.beginPath();
ctx.rect(400, 200, 100, 50);
ctx.stroke();

ctx.fillText('sum=num1 + num2', 450, 225);

ctx.beginPath();
ctx.moveTo(450, 250);
ctx.lineTo(450, 275);
ctx.stroke();

ctx.beginPath();
ctx.ellipse(450, 300, 50, 25, 0, 0, Math.PI * 2);
ctx.stroke();

ctx.fillText('Конец', 450, 300);
