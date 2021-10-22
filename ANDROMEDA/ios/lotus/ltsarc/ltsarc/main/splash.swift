//
//  splash.swift
//  ltsarc
//
//  Created by TRIAL on 06/10/21.
//

import UIKit

class splash: UIViewController {
    var seconds = 2
        var myTimer: Timer?
        override func viewDidLoad() {
        super.viewDidLoad()
            startCountdown()
       
    }
    @objc func doSegue(){
        performSegue(withIdentifier: "segmain", sender: self)
       // self.performSegue(withIdentifier: "segmain", sender: self)
    }
    func startCountdown() {
            myTimer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { [weak self] timer in
                self?.seconds -= 1
                if self?.seconds == 0 {
                  //  print("Go!")
                    self?.doSegue()
                    timer.invalidate()
                } else if let seconds = self?.seconds {
                    print(seconds)
                }
            }
        }

        deinit {
            // ViewController going away.  Kill the timer.
            myTimer?.invalidate()
        }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
