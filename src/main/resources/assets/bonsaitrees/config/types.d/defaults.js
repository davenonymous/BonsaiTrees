var stickAmount = 3;
var stickChance = 50;

var plankChance = 25;
var plankAmount = 1;

var logChance = 75;
var logAmount = 1;

var leafChance = 10;
var leafAmount = 1;

var saplingAmount = 1;
var saplingChance = 5;

var fruitAmount = 2;
var fruitChance = 20;

var defaultGrowTime = 600;

// ##########################################################################

var TreeTypeSimple = Java.type("org.dave.bonsaitrees.trees.TreeTypeSimple");
var TreeTypeRegistry = Java.type("org.dave.bonsaitrees.trees.TreeTypeRegistry");

var isEnabled = function() {
    return true;
}