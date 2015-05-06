/********* AMapNavigation.m Cordova Plugin Implementation *******/

#import <UIKit/UIKit.h>
#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>

#import <AMapNaviKit/AMapNaviKit.h>
#import <AMapNaviKit/MAMapKit.h>


@interface AMapNavigation : CDVPlugin <MAMapViewDelegate, AMapNaviViewControllerDelegate, AMapNaviViewControllerDelegate>{
    // Member variables go here.
    NSString* callbackId;
}

@property (nonatomic, strong)NSString*                  amapApiKey;
@property (nonatomic, strong)MAMapView*                 mapView;
@property (nonatomic, strong)AMapNaviManager*           naviManager;
@property (nonatomic, strong)AMapNaviViewController*    naviViewController;
@property (nonatomic, strong)AMapNaviPoint*             startPoint;
@property (nonatomic, strong)AMapNaviPoint*             endPoint;
@property (nonatomic, strong)AVSpeechSynthesizer*       speechSynthesizer;

- (void)navigation:(CDVInvokedUrlCommand*)command;
- (void)returnSuccess;
- (void)returnError:(NSString*) message;
@end

@implementation AMapNavigation

- (void)navigation:(CDVInvokedUrlCommand*)command
{
    callbackId = command.callbackId;
    [AMapNaviServices sharedServices].apiKey = [self amapApiKey];
    [MAMapServices sharedServices].apiKey = [self amapApiKey];
    
    CGFloat startLng = [[command.arguments objectAtIndex:0] doubleValue];
    CGFloat startLat = [[command.arguments objectAtIndex:1] doubleValue];
    CGFloat endLng = [[command.arguments objectAtIndex:2] doubleValue];
    CGFloat endLat = [[command.arguments objectAtIndex:3] doubleValue];
    
    [self initMapView];
    [self initManager];
    [self initNaviViewController];
    
    AMapNaviPoint* startPoint = [AMapNaviPoint locationWithLatitude:startLat longitude:startLng];
    AMapNaviPoint* endPoint = [AMapNaviPoint locationWithLatitude:endLat longitude:endLng];
    [self calculateRoute:startPoint endPoint:endPoint];
    
    
    //    CDVPluginResult* pluginResult = nil;
    //    NSString* echo = [command.arguments objectAtIndex:0];
    //
    //    if (echo != nil && [echo length] > 0) {
    //        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    //    } else {
    //        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    //    }
    //
    //    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)returnSuccess{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

- (void)returnError:(NSString *)message{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

- (NSString*)amapApiKey{
    if(!_amapApiKey){
        CDVViewController* viewController = (CDVViewController*)self.viewController;
        _amapApiKey = [viewController.settings objectForKey:@"amapapikey"];
    }
    return _amapApiKey;
}

- (void)initMapView{
    if(self.mapView == nil){
        self.mapView = [[MAMapView alloc] initWithFrame:self.webView.bounds];
    }
    self.mapView.frame = self.webView.bounds;
    self.mapView.delegate = self;
}

- (void)initManager{
    if(self.naviManager == nil){
        _naviManager = [[AMapNaviManager alloc] init];
        [_naviManager setDelegate:self];
    }
}

- (void)initSpeecher{
    if(self.speechSynthesizer == nil){
        self.speechSynthesizer = [[AVSpeechSynthesizer alloc] init];
    }
}

- (void)initNaviViewController{
    if(_naviViewController == nil){
        _naviViewController = [[AMapNaviViewController alloc] initWithMapView:self.mapView delegate:self];
    }
}

- (void)calculateRoute:(AMapNaviPoint*)startPoint endPoint:(AMapNaviPoint*)endPoint{
    NSArray* startPoints = @[startPoint];
    NSArray* endPoints = @[endPoint];
    
//    [self.naviManager calculateDriveRouteWithEndPoints:endPoints wayPoints:nil drivingStrategy:0];
    [self.naviManager calculateDriveRouteWithStartPoints:startPoints endPoints:endPoints wayPoints:nil drivingStrategy:0];
}


- (void)AMapNaviManager:(AMapNaviManager *)naviManager didPresentNaviViewController:(UIViewController *)naviViewController
{
    [self.naviManager startGPSNavi];
}


- (void)AMapNaviManagerOnCalculateRouteSuccess:(AMapNaviManager *)naviManager
{
    [self initSpeecher];
    [self.naviManager presentNaviViewController:self.naviViewController animated:YES];
}


- (void)AMapNaviManager:(AMapNaviManager *)naviManager onCalculateRouteFailure:(NSError *)error
{
//    [self AMapNaviManager:naviManager onCalculateRouteFailure:error];
    [self returnError:[NSString stringWithFormat:@"规划路径错误:%@", error]];
}



#pragma mark - AManNaviViewController Delegate

- (void)AMapNaviViewControllerCloseButtonClicked:(AMapNaviViewController *)naviViewController
{
    if(self.speechSynthesizer.isSpeaking){
        [self.speechSynthesizer stopSpeakingAtBoundary:AVSpeechBoundaryImmediate];
    }
    [self.naviManager stopNavi];
    [self.naviManager dismissNaviViewControllerAnimated:YES];
    [self returnSuccess];
}


- (void)AMapNaviViewControllerMoreButtonClicked:(AMapNaviViewController *)naviViewController
{
    if (self.naviViewController.viewShowMode == AMapNaviViewShowModeCarNorthDirection)
    {
        self.naviViewController.viewShowMode = AMapNaviViewShowModeMapNorthDirection;
    }
    else
    {
        self.naviViewController.viewShowMode = AMapNaviViewShowModeCarNorthDirection;
    }
}


- (void)AMapNaviViewControllerTrunIndicatorViewTapped:(AMapNaviViewController *)naviViewController
{
    [self.naviManager readNaviInfoManual];
}


- (void)AMapNaviManager:(AMapNaviManager *)naviManager error:(NSError *)error
{
    [self returnError:[NSString stringWithFormat:@"error:%@", error]];
}

- (void)AMapNaviManager:(AMapNaviManager *)naviManager didDismissNaviViewController:(UIViewController *)naviViewController
{
    NSLog(@"didDismissNaviViewController");
}


- (void)AMapNaviManagerNeedRecalculateRouteForTrafficJam:(AMapNaviManager *)naviManager
{
    NSLog(@"NeedReCalculateRouteForTrafficJam");
}

- (void)AMapNaviManagerNeedRecalculateRouteForYaw:(AMapNaviManager *)naviManager
{
    [self speak: @"您已偏航，正在重新规划路径"];
    [self.naviManager recalculateDriveRouteWithDrivingStrategy:AMapNaviDrivingStrategyDefault];
}

- (void)AMapNaviManager:(AMapNaviManager *)naviManager didStartNavi:(AMapNaviMode)naviMode
{
    NSLog(@"didStartNavi");
}

- (void)AMapNaviManagerDidEndEmulatorNavi:(AMapNaviManager *)naviManager
{
    NSLog(@"DidEndEmulatorNavi");
}

- (void)AMapNaviManagerOnArrivedDestination:(AMapNaviManager *)naviManager
{
    [self returnSuccess];
}

- (void)AMapNaviManager:(AMapNaviManager *)naviManager onArrivedWayPoint:(int)wayPointIndex
{
    NSLog(@"onArrivedWayPoint");
}

- (void)AMapNaviManager:(AMapNaviManager *)naviManager didUpdateNaviLocation:(AMapNaviLocation *)naviLocation
{
    //    NSLog(@"didUpdateNaviLocation");
}

- (BOOL)AMapNaviManagerGetSoundPlayState:(AMapNaviManager *)naviManager
{
    //    NSLog(@"GetSoundPlayState");
    
    return 0;
}

- (void)AMapNaviManager:(AMapNaviManager *)naviManager playNaviSoundString:(NSString *)soundString soundStringType:(AMapNaviSoundType)soundStringType
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        [self speak: soundString];
    });
    // NSLog(@"playNaviSoundString:{%ld:%@}", (long)soundStringType, soundString);
}

- (void)AMapNaviManagerDidUpdateTrafficStatuses:(AMapNaviManager *)naviManager
{
    NSLog(@"DidUpdateTrafficStatuses");
}

- (void)speak:(NSString*)text{
    if(self.speechSynthesizer.isSpeaking){
        [self.speechSynthesizer stopSpeakingAtBoundary:AVSpeechBoundaryImmediate];
    }
    AVSpeechUtterance* utterance = [[AVSpeechUtterance alloc] initWithString:soundString];
    utterance.rate = 0.1;
    utterance.voice = [AVSpeechSynthesisVoice voiceWithLanguage:@"zh_CN"];
    [self.speechSynthesizer speakUtterance:utterance];
}

@end