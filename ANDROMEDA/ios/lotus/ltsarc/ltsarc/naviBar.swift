//
//  naviBar.swift
//  ltsarc
//
//  Created by TRIAL on 28/08/21.
//

import UIKit

class NaviBar: UIViewController {
   // let tabBar = UITabBarController()
    var actionButton : ActionButton!
    
    private let button: UIButton = {
        let button = UIButton(frame: CGRect(x: 0, y: 0, width: 200, height: 50))
        button.setTitle("on", for: .normal)
        button.backgroundColor = .gray
        button.setTitleColor(.black, for: .normal)
        return button
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
 
      
       
     //   view.addSubview(button)
       // button.addTarget(self, action: #selector(adNav), for: .touchUpInside)
        
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        button.center = view.center
    }
    fileprivate func generateNavController(vc: UIViewController, title: String, image: UIImage) -> UINavigationController{
        let navController = UINavigationController(rootViewController: vc)
        navController.title = title
        if(!title.isEmpty){
            navController.tabBarItem.image = image
        }
       
       // navController.tabBarItem.image = image
        return navController
    }
   
        @objc func adNav(){
    
            let button = UIButton(frame: CGRect(x: 12,  y: 20,  width: 25,  height: 25))
                  // button.setTitle("Back",for: .normal)
                   button.setImage(UIImage(named: "bakcwhite.png"), for: .normal)
                   button.setTitleColor(.systemBlue, for: .normal)
    
            button.addTarget(self, action: #selector(self.adNav), for: .touchUpInside)
                   self.view.addSubview(button)
    
           let tabBar = UITabBarController()
    
           let nav1 = PriceViController()
           let nav2 = PromoVController()
           let nav3 = ViewController()
           let nav4 = ContactUsVController()
           let nav5 = SettingVController()
    
    
           nav1.title = "Price"
           nav2.title = "Promo"
           nav3.title = "Qr"
           nav4.title = "Contact Us"
           nav5.title = "Setting"
    
            nav1.tabBarItem.image = UIImage(imageLiteralResourceName: "iclts")
            nav2.tabBarItem.image = UIImage(imageLiteralResourceName: "icpromo")
            nav4.tabBarItem.image = UIImage(imageLiteralResourceName: "iccall")
            nav5.tabBarItem.image = UIImage(imageLiteralResourceName: "icgear")
    
           tabBar.setViewControllers([nav1,nav2,nav3,nav4,nav5], animated: false)
    
           tabBar.modalPresentationStyle = .fullScreen
    
           present(tabBar, animated: true)
      //      self.setupButtons()
       }
    
    
}

class PriceViController: UIViewController{
     override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .blue
        title = "Price"
    }
}

class PromoVController: UIViewController{
     override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .green
        title = "Promo"
    }
}

class Bck: UIViewController{
     override func viewDidLoad() {
        return
    }
}
class EmptyVController: UIViewController{
     override func viewDidLoad() {
        super.viewDidLoad()
       // view.backgroundColor = .green
       // title = "Promo"
    }
}
class ContactUsVController: UIViewController{
     override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .gray
        title = "Contact Us"
    }
}

class SettingVController: UIViewController{
     override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .magenta
        title = "Setting"
    }
}
