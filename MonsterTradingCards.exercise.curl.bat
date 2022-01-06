@echo off

REM --------------------------------------------------
REM Monster Trading Cards Game
REM --------------------------------------------------
title Monster Trading Cards Game
echo CURL Testing for Monster Trading Cards Game
echo.

REM --------------------------------------------------
echo 1) Create Users (Registration)
REM Create User
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo.
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}"
echo.
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"admin\",    \"Password\":\"istrator\"}"
echo.

echo should fail:
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo.
curl -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"different\"}"
echo. 
echo.

REM --------------------------------------------------
echo 2) Login Users
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo.
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}"
echo.
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"admin\",    \"Password\":\"istrator\"}"
echo.

echo should fail:
curl -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"different\"}"
echo.
echo.

REM --------------------------------------------------
echo 3) create packages (done by "admin")
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"monstertype\":\"Goblin\", \"elementtype\":\"Water\", \"Damage\": 10.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2b\", \"monstertype\":\"Dragon\", \"elementtype\":\"Fire\", \"Damage\": 10.0}, {\"Id\":\"e85e3976-7c86-4d06-9a22-641c2019a79f\", \"monstertype\":\"Spell\", \"elementtype\":\"Water\", \"Damage\": 22.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"monstertype\":\"Knight\", \"elementtype\":\"Fire\", \"Damage\": 45.0}, {\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\", \"monstertype\":\"Kraken\", \"elementtype\":\"Fire\", \"Damage\": 23.0}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"644808c2-f87a-4600-b313-122b02322fd5\", \"monstertype\":\"Goblin\", \"elementtype\":\"Water\", \"Damage\": 11.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2c\", \"monstertype\":\"Dragon\", \"elementtype\":\"Fire\", \"Damage\": 14.0}, {\"Id\":\"e85e3976-7c86-4d06-9a12-641c2019a79f\", \"monstertype\":\"Spell\", \"elementtype\":\"Normal\", \"Damage\": 21.0}, {\"Id\":\"4ec8b269-0dfa-4f97-809a-2c63fe2a0025\", \"monstertype\":\"Knight\", \"elementtype\":\"Fire\", \"Damage\": 45.0}, {\"Id\":\"f8043c23-1534-4487-b66b-238e0c3c39b5\", \"monstertype\":\"Kraken\", \"elementtype\":\"Water\", \"Damage\": 21.0}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"b017ee50-1c14-44e2-bfd6-2c0c5653a37c\", \"monstertype\":\"Goblin\", \"elementtype\":\"Fire\", \"Damage\": 18.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2s\", \"monstertype\":\"Dragon\", \"elementtype\":\"Fire\", \"Damage\": 13.0}, {\"Id\":\"e85e3976-7c86-4d06-9a11-641c2019a79f\", \"monstertype\":\"Spell\", \"elementtype\":\"Fire\", \"Damage\": 25.0}, {\"Id\":\"1d3f175b-c067-4359-989d-96562bfa382c\", \"monstertype\":\"Knight\", \"elementtype\":\"Fire\", \"Damage\": 45.0}, {\"Id\":\"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\", \"monstertype\":\"Kraken\", \"elementtype\":\"Water\", \"Damage\": 22.5}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"ed1dc1bc-f0aa-4a0c-8d43-1402189b33c8\", \"monstertype\":\"Goblin\", \"elementtype\":\"Normal\", \"Damage\": 15.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e4l\", \"monstertype\":\"Dragon\", \"elementtype\":\"Normal\", \"Damage\": 11.0}, {\"Id\":\"e85e3976-7c86-4d06-9a88-641c2019a79f\", \"monstertype\":\"Spell\", \"elementtype\":\"Normal\", \"Damage\": 29.0}, {\"Id\":\"f3fad0f2-a1af-45df-b80d-2e48825773d9\", \"monstertype\":\"Knight\", \"elementtype\":\"Fire\", \"Damage\": 45.0}, {\"Id\":\"8c20639d-6400-4534-bd0f-ae563f11f57a\", \"monstertype\":\"Kraken\", \"elementtype\":\"Fire\", \"Damage\": 25.0}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"d7d0cb94-2cbf-4f97-8ccf-9933dc5354b8\", \"monstertype\":\"Goblin\", \"elementtype\":\"Fire\", \"Damage\": 14.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36el4\", \"monstertype\":\"Dragon\", \"elementtype\":\"Water\", \"Damage\": 22.0}, {\"Id\":\"e85e3976-7c86-4d06-9m98-641c2019a79f\", \"monstertype\":\"Spell\", \"elementtype\":\"Water\", \"Damage\": 22.0}, {\"Id\":\"951e886a-0fbf-425d-8df5-af2ee4830d85\", \"monstertype\":\"Knight\", \"elementtype\":\"Fire\", \"Damage\": 45.0}, {\"Id\":\"dcd93250-25a7-4dca-85da-cad2789f7198\", \"monstertype\":\"Kraken\", \"elementtype\":\"Normal\", \"Damage\": 22.0}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"b2237eca-0271-43bd-87f6-b22f70d42ca4\", \"monstertype\":\"Goblin\", \"elementtype\":\"Water\", \"Damage\": 12.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36en3\", \"monstertype\":\"Dragon\", \"elementtype\":\"Normal\", \"Damage\": 43.0}, {\"Id\":\"e85e3976-7c86-4d06-9k92-641c2019a79f\", \"monstertype\":\"Spell\", \"elementtype\":\"Fire\", \"Damage\": 20.0}, {\"Id\":\"fc305a7a-36f7-4d30-ad27-462ca0445649\", \"monstertype\":\"Knight\", \"elementtype\":\"Fire\", \"Damage\": 45.0}, {\"Id\":\"84d276ee-21ec-4171-a509-c1b88162831c\", \"monstertype\":\"Kraken\", \"elementtype\":\"Water\", \"Damage\": 24.3}]"
echo.
echo.

REM --------------------------------------------------
echo 4) acquire packages kienboec
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
echo should fail (no money):
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 5) acquire packages altenhof
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo should fail (no package):
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 6) add new packages
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"845f0dnd-37d0-426e-1032-43fc3ac83c10\", \"monstertype\":\"Dragon\", \"elementtype\":\"Normal\", \"Damage\": 12.0}, {\"Id\":\"99f8f8dc-f321-4a95-aa2c-782823f36en3\", \"monstertype\":\"Goblin\", \"elementtype\":\"Normal\", \"Damage\": 11.0}, {\"Id\":\"e85e3976-7c86-bg06-9k92-641c2019a79f\", \"monstertype\":\"Ork\", \"elementtype\":\"Water\", \"Damage\": 10.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6h3-68c5ab389311\", \"monstertype\":\"Goblin\", \"elementtype\":\"Fire\", \"Damage\": 22.0}, {\"Id\":\"dfdd758f-649c-40f9-papa-8657f4b34399\", \"monstertype\":\"Kraken\", \"elementtype\":\"Water\", \"Damage\": 29.3}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"845f0dnd-37d0-426e-8032-43fc3ac83c10\", \"monstertype\":\"Goblin\", \"elementtype\":\"Water\", \"Damage\": 12.0}, {\"Id\":\"99f8f8dc-f123-4a95-aa2c-782823f36en3\", \"monstertype\":\"Elves\", \"elementtype\":\"Normal\", \"Damage\": 31.0}, {\"Id\":\"e85e3976-7c86-4g06-9k92-641c2019a79f\", \"monstertype\":\"Wizzard\", \"elementtype\":\"Normal\", \"Damage\": 7.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6ad-68c5ab389311\", \"monstertype\":\"Elves\", \"elementtype\":\"Water\", \"Damage\": 21.0}, {\"Id\":\"dfdd758f-649c-40f9-mama-8657f4b34399\", \"monstertype\":\"Goblin\", \"elementtype\":\"Normal\", \"Damage\": 28.0}]"
echo.
curl -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Basic admin-mtcgToken" -d "[{\"Id\":\"845f0dnd-37d0-426e-8843-43fc3ac83c10\", \"monstertype\":\"Knight\", \"elementtype\":\"Fire\", \"Damage\": 12.0}, {\"Id\":\"99f8f8dc-f453-4a95-aa2c-782823f36en3\", \"monstertype\":\"Spell\", \"elementtype\":\"Normal\", \"Damage\": 12.0}, {\"Id\":\"e85e3976-7c86-as06-9k92-641c2019a79f\", \"monstertype\":\"Spell\", \"elementtype\":\"Fire\", \"Damage\": 13.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6f4-68c5ab389311\", \"monstertype\":\"Spell\", \"elementtype\":\"Normal\", \"Damage\": 12.0}, {\"Id\":\"dfdd758f-649c-40f9-baba-8657f4b34399\", \"monstertype\":\"Dragon\", \"elementtype\":\"Fire\", \"Damage\": 24.3}]"
echo.
echo.

REM --------------------------------------------------
echo 7) acquire newly created packages altenhof
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo should fail (no money):
curl -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 8) show all acquired cards kienboec
curl -X GET http://localhost:10001/cards --header "Authorization: Basic kienboec-mtcgToken"
echo should fail (no token)
curl -X GET http://localhost:10001/cards 
echo.
echo.

REM --------------------------------------------------
echo 9) show all acquired cards altenhof
curl -X GET http://localhost:10001/cards --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 10) show unconfigured deck
curl -X GET http://localhost:10001/deck --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.


REM --------------------------------------------------
echo 11) configure deck
curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2b\", \"e85e3976-7c86-4d06-9a22-641c2019a79f\", \"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\"]"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic kienboec-mtcgToken"
echo.

curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "[\"d7d0cb94-2cbf-4f97-8ccf-9933dc5354b8\", \"99f8f8dc-e25e-4a95-aa2c-782823f36el4\", \"e85e3976-7c86-4d06-9m98-641c2019a79f\", \"951e886a-0fbf-425d-8df5-af2ee4830d85\"]"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.
echo should fail and show original from before:
curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "[\"845f0dnd-37d0-426e-8843-43fc3ac83c10\", \"99f8f8dc-f453-4a95-aa2c-782823f36en3\", \"e85e3976-7c86-as06-9k92-641c2019a79f\", \"1cb6ab86-bdb2-47e5-b6f4-68c5ab389311\"]"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.
echo should fail ... only 3 cards set
curl -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "[\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"d60e23cf-2238-4d49-844f-c7589ee5342e\"]"
echo.


REM --------------------------------------------------
echo 12) show configured deck 
curl -X GET http://localhost:10001/deck --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/deck --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 13) show configured deck different representation
echo kienboec
curl -X GET http://localhost:10001/deck?format=plain --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.
echo altenhof
curl -X GET http://localhost:10001/deck?format=plain --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 14) edit user data
echo.
curl -X GET http://localhost:10001/users/kienboec --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/users/altenhof --header "Authorization: Basic altenhof-mtcgToken"
echo.
curl -X PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
echo.
curl -X PUT http://localhost:10001/users/altenhof --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "{\"Name\": \"Altenhofer\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}"
echo.
curl -X GET http://localhost:10001/users/kienboec --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/users/altenhof --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.
echo should fail:
curl -X GET http://localhost:10001/users/altenhof --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/users/kienboec --header "Authorization: Basic altenhof-mtcgToken"
echo.
curl -X PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "{\"Name\": \"Hoax\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
echo.
curl -X PUT http://localhost:10001/users/altenhof --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Name\": \"Hoax\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}"
echo.
curl -X GET http://localhost:10001/users/someGuy  --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 15) stats
curl -X GET http://localhost:10001/stats --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/stats --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 16) scoreboard
curl -X GET http://localhost:10001/score --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 17) battle
start /b "kienboec battle" curl -X POST http://localhost:10001/battles --header "Authorization: Basic kienboec-mtcgToken"
start /b "altenhof battle" curl -X POST http://localhost:10001/battles --header "Authorization: Basic altenhof-mtcgToken"
ping localhost -n 10 >NUL 2>NUL

REM --------------------------------------------------
echo 18) Stats
echo kienboec
curl -X GET http://localhost:10001/stats --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo altenhof
curl -X GET http://localhost:10001/stats --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 19) scoreboard
curl -X GET http://localhost:10001/score --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 20) trade
echo check trading deals
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo create trading deal
curl -X POST http://localhost:10001/tradings --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Id\": \"6cd85277-4590-49d4-b0cf-ba0a921faad0\", \"CardToTrade\": \"99f8f8dc-e25e-4a95-aa2c-782823f36e2c\", \"WantsMonster\": \"true\",  \"WantsSpell\": \"false\", \"MinimumDamage\": 15}"
echo.
echo check trading deals
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo delete trading deals
curl -X DELETE http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Authorization: Basic kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 21) check trading deals
curl -X GET http://localhost:10001/tradings  --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X POST http://localhost:10001/tradings --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "{\"Id\": \"6cd85277-4590-49d4-b0cf-ba0a921faad0\", \"CardToTrade\": \"b017ee50-1c14-44e2-bfd6-2c0c5653a37c\", \"WantsMonster\": \"true\",  \"WantsSpell\": \"false\", \"MinimumDamage\": 15}"
echo check trading deals
curl -X GET http://localhost:10001/tradings  --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/tradings  --header "Authorization: Basic altenhof-mtcgToken"
echo.
echo try to trade with yourself (should fail)
curl -X POST http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Content-Type: application/json" --header "Authorization: Basic kienboec-mtcgToken" -d "\"4ec8b269-0dfa-4f97-809a-2c63fe2a0025\""
echo.
echo try to trade
echo.
curl -X POST http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Content-Type: application/json" --header "Authorization: Basic altenhof-mtcgToken" -d "\"1cb6ab86-bdb2-47e5-b6ad-68c5ab389311\""
echo.
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic kienboec-mtcgToken"
echo.
curl -X GET http://localhost:10001/tradings --header "Authorization: Basic altenhof-mtcgToken"
echo.

REM --------------------------------------------------
echo end...

REM this is approx a sleep 
ping localhost -n 100 >NUL 2>NUL
@echo on
