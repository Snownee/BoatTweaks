// priority: 0

console.info('Hello, World! (You will see this line every time server resources reload)')

ItemEvents.entityInteracted('redstone', event => {
    console.log(event.target.type)
    //if (event.target.type !== 'entity.minecraft.boat')
    //    return
    let settings = BoatSettings.DEFAULT.copy()
    settings.forwardForce = 0.5
    BoatTweaks.setBoatSettings(event.target, settings)
    event.item.shrink(1)
})
