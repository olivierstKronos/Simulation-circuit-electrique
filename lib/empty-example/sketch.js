class Resistor{
  
}



function setup() {
  // put setup code here
  let resisteur = {x:400, y:100, taille:50};
  let batterie = {x:100-10, y:100 - 20, sec_width:20, sec_height:40}

  array= [resisteur, batterie];
  createCanvas(windowWidth, windowHeight);

}
let array;

function draw() {
  // put drawing code here
  background(0);
  createResistor(array[0]);
  createBatterie(array[1]);
  createFils();
  
}

function createResistor(resisteur){
  noStroke();
  fill('#299bf6');
  circle(resisteur.x, resisteur.y, resisteur.taille);
  fill('#a358a8');
  triangle(resisteur.x - 80, resisteur.y, resisteur.x - 30, resisteur.y - 20, resisteur.x - 30, resisteur.y + 20);
  triangle(resisteur.x + 80, resisteur.y, resisteur.x + 30, resisteur.y + 20, resisteur.x + 30, resisteur.y - 20);
}

function createBatterie(batterie){
  noStroke();
  fill('#e0636c');
  rect(batterie.x - batterie.sec_width * 2, batterie.y, batterie.sec_width, batterie.sec_height);
  fill('#be6781');
  rect(batterie.x - batterie.sec_width, batterie.y, batterie.sec_width, batterie.sec_height);
  fill('#9c6a96');
  rect(batterie.x, batterie.y, batterie.sec_width, batterie.sec_height);
  fill('#7a6dab');
  rect(batterie.x + batterie.sec_width, batterie.y, batterie.sec_width, batterie.sec_height);
  fill('#5771c1');
  rect(batterie.x + batterie.sec_width * 2, batterie.y, batterie.sec_width, batterie.sec_height);
}

function createFils(){
  noFill();
  stroke('orange');
  strokeWeight(10);
  strokeCap(ROUND);
  line(20, 30, 200, 30);
  bezier(200, 30, 210, 30, 220, 40, 220, 50);
  line(220, 50, 220, 150);

}

