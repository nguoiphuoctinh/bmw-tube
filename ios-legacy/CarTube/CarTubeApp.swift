//
//  CarTubeApp.swift
//  CarTube
//
//  Created by Rory Madden on 22/12/22.
//

import SwiftUI

@main
struct CarTubeApp: App {
    init() {
        registerDefaults()
        ParkedGuard.shared.requestPermission()   // BMW Tube: GPS for the parked-only lock
        
        if UserDefaults.standard.bool(forKey: "LockScreenDimmingOn") {
            CarPlaySingleton.shared.saveInitialBrightness()
            registerForScreenOffNotification {
                CarPlaySingleton.shared.showScreenOffWarning()
                CarPlaySingleton.shared.setLowBrightness()
            }
            registerForUnlockNotification {
                CarPlaySingleton.shared.restoreBrightness()
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL { url in
                let id = url.absoluteString.replacingOccurrences(of: "^cartube?://", with: "", options: .regularExpression)
                if id.count != 11 {
                    UIApplication.shared.alert(body: "Invalid YouTube link.", window: .main)
                } else {
                    let youtube = YT_EMBED + id
                    CarPlaySingleton.shared.dontAskAboutLastPlaying()
                    CarPlaySingleton.shared.loadUrl(youtube)
                }
            }.onAppear {
                checkNewVersions()
            }
        }
    }
    
    func registerDefaults() {
        UserDefaults.standard.register(defaults: [
            "SponsorBlockOn": false,
            "AgeRestrictBypassOn": false,
            "AdBlockerOn": false,
            "Zoom": 80,
            "ScreenPersistenceOn": true,
            "LockScreenDimmingOn": true
        ])
    }
    
    func checkNewVersions() {
        // BMW Tube: disabled - upstream releases do not have the parked-only lock.
    }
}
