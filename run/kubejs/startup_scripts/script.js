// priority: 0

console.info('Hello, World! (You will only see this line once in console, during startup)')

StartupEvents.postInit(event => {
    BoatTweaks.customSpecialBlocks.put(Blocks.GLASS, 20)
})

BoatTweaksEvents.onSpecialBlock('glass', event => {
    console.log(event.pos)
    console.log(event.boat)
})
