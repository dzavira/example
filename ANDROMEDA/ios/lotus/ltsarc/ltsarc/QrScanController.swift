//
//  QrScanController.swift
//  ltsarc
//
//  Created by TRIAL on 06/10/21.
//

import AVFoundation
import UIKit

extension QrScanController: AVCaptureMetadataOutputObjectsDelegate {
    
}

class QrScanController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .red
        self.addBack()
        
       
    }
    @objc func btBack(_ sender:UIButton!)
        {
      //  let nabBar = NaviBar()
       // self.navigationController?.pushViewController(nabBar, animated: false)
        performSegue(withIdentifier: "segHalUtama", sender: self)
        }
    func addBack(){
        let button = UIButton(frame: CGRect(x: 12,  y: 20,  width: 25,  height: 25))
              // button.setTitle("Back",for: .normal)
               button.setImage(UIImage(named: "bakcwhite.png"), for: .normal)
               button.setTitleColor(.systemBlue, for: .normal)
       
        button.addTarget(self, action: #selector(btBack(_:)), for: .touchUpInside)
               self.view.addSubview(button)
        
    }

}
