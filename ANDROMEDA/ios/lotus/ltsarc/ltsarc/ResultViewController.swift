//
//  ResultViewController.swift
//  ltsarc
//
//  Created by TRIAL on 26/08/21.
//


import UIKit
import WebKit

class ResultViewController: UIViewController, WKUIDelegate {
    var webView: WKWebView!
    var actionButton : ActionButton!
    var urlHasil: String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if urlHasil.isEmpty
        {
            urlHasil="http://merdeka.com/"
        }

        let myURL = URL(string:urlHasil)

        let myRequest = URLRequest(url: myURL!)
            webView.load(myRequest)
        
        
    }
    
    override func loadView() {
         let webConfiguration = WKWebViewConfiguration()
         webView = WKWebView(frame: .zero, configuration: webConfiguration)
         webView.uiDelegate = self
         view = webView
        
       
      //  setupButtons()
        addBack()
   //     setUpNavBar()
      }
    func setUpNavBar(){
        //For title in navigation bar
        self.navigationController?.view.backgroundColor = UIColor.white
        self.navigationController?.view.tintColor = UIColor.orange
        self.navigationItem.title = "About Us"

        //For back button in navigation bar
        let backButton = UIBarButtonItem()
        backButton.title = "Back"
        self.navigationController?.navigationBar.topItem?.backBarButtonItem = backButton
    }
    func addBack(){
        let button = UIButton(frame: CGRect(x: 12,
                                                   y: 20,
                                                   width: 25,
                                                   height: 25))
              // button.setTitle("Back",for: .normal)
        button.setImage(UIImage(named: "backblue.png"), for: .normal)
               button.setTitleColor(.systemBlue,
                                    for: .normal)

        button.addTarget(self, action: #selector(self.btBack), for: .touchUpInside)

               self.view.addSubview(button)
        
    }
    func backAction() -> Void {
        performSegue(withIdentifier: "segKembali", sender: self)
    }
    func setupButtons(){
//        let about = ActionButtonItem(title: "About", image: #imageLiteral(resourceName: "about"))
//        about.action = { item in self.about() }
        actionButton = ActionButton(attachedToView: self.view, items: [])
      //  actionButton.setTitle("<", forState: UIControl.State())
        actionButton.setImage(#imageLiteral(resourceName: "scan"),forState: UIControl.State())
        actionButton.action = { button in self.pindah() }
    }
    func pindah(){
        performSegue(withIdentifier: "segKembali", sender: self)
        }
    @objc func btBack(_ sender:UIButton!)
        {
        performSegue(withIdentifier: "segHalUtama", sender: self) //segkembali
        }
    func found(code: String) {
        let ac = UIAlertController(title: "Result", message: code, preferredStyle: .alert)
        ac.addAction(UIAlertAction(title: "OK", style: .default, handler: {(action: UIAlertAction!) in
        }))
        present(ac, animated: true)
    }
}
